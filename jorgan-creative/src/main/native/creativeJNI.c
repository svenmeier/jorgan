#include <stdio.h>
#include "jorgan.creative.SoundFontManager.h"
#include "SFMAN.H"

  /** One global reference to the Sound Font Manager API */
  PSFMANL101API pSFManager101API = NULL;
  
  JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  
    JNIEnv *env;
    
    if ((*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_2)) {
      return JNI_ERR;
    }

    // Load Sound Font Manager DLL
    HANDLE handleSFMAN32 = LoadLibrary(SF_MASTER_MANAGER_FILENAME) ;
    if (handleSFMAN32 == NULL) { 
      return JNI_ERR;
    }
 
    // Lookup function table
    PSFMANAGER pSFManager = (PSFMANAGER)GetProcAddress(handleSFMAN32, SF_FUNCTION_TABLE_NAME);
    if (pSFManager == NULL) { 
      return JNI_ERR;
    }
	 
    LRESULT rc = pSFManager->SF_QueryInterface(ID_SFMANL101API, &pSFManager101API );
    if (rc != SFERR_NOERR) {
      return JNI_ERR;
    }    
    
    return JNI_VERSION_1_2;
  }

  JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
  }
  
  /**
   * Helper for throwing an Exception
   * @return FALSE
   */
  int throwException(JNIEnv* env, char* exception, char* pattern, ...) {

    // Assemble message from pattern and variable number of parameters
    va_list args;
    va_start(args, pattern);
    char msg[256];
    vsnprintf(msg, 256, pattern, args);
    va_end(args);

    // create the exception  
    jclass exceptionClass = (*env)->FindClass(env, exception);
    (*env)->ThrowNew(env, exceptionClass, msg);
    
    // report FALSE
    return FALSE;
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getNumDevices
   * Signature: ()I
   */
  JNIEXPORT jint JNICALL Java_jorgan_creative_SoundFontManager_getNumDevices (JNIEnv *env, jobject obj) {
	
    // ask for it
    WORD num = 0;
    LRESULT rc = pSFManager101API->SF_GetNumDevs(&num);
    if (rc != SFERR_NOERR) {
      return throwException(env, "java/lang/Error", "rc %d", rc);
    }
    
    // done
    return num;
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getDeviceName
   * Signature: (I)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDeviceName(JNIEnv *env, jobject obj, jint device) {

    // prepare result data structure	    
    CSFCapsObject caps;
    memset(&caps, 0, sizeof(caps));
    caps.m_SizeOf = sizeof(caps);

    // ask for the data
    LRESULT rc = pSFManager101API->SF_GetDevCaps(device, &caps);
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }

    // make sure the device name has a trailing 0 and convert into jstring
    caps.m_DevName[39] = 0;

    jstring result = (*env)->NewStringUTF(env, caps.m_DevName);

    // done
    return result;
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    open
   * Signature: (I)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_open(JNIEnv *env, jobject obj, jint device) {

    // open the device
    LRESULT rc = pSFManager101API->SF_Open(device);
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }
	  
    // done
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    close
   * Signature: (I)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_close(JNIEnv *env, jobject obj, jint device) {

    // close it
    LRESULT rc = pSFManager101API->SF_Close(device);
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }
	  
    // done	  
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    isBankUsed
   * Signature: (II)Z
   */
  JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isBankUsed(JNIEnv *env, jobject obj, jint device, jint bank) {

    // ask manager - after the call:
    // if bank2result ... then bank contains ...
    //   =bank | loaded presets or waveforms
    //  !=bank | loaded presets or waveforms loaded form another bank
    //   -1    | no loaded presets or waveforms      

    WORD bank2result = bank;

    LRESULT rc = pSFManager101API->SF_IsMIDIBankUsed(device, &bank2result);
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc == SFERR_BANK_INDEX_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", bank);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }

    return bank2result==65535 ? JNI_FALSE : JNI_TRUE; 
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getBankDescriptor
   * Signature: (II)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getBankDescriptor(JNIEnv *env, jobject obj, jint device, jint bank) {
	
    // prepare midi descriptor
    CSFMIDILocation midiLocation;
    midiLocation.m_BankIndex = bank;
    midiLocation.m_PresetIndex = 0; // isn't used

    // prepare buffer TODO this is good for a description with up to 256-2 characters in length
    char desc[256];
    CSFBufferObject buffer;
    memset(&buffer, 0, sizeof(buffer));
    buffer.m_Size = strlen(desc);
    buffer.m_Buffer = desc;

    // do the call
    LRESULT rc = pSFManager101API->SF_GetLoadedBankDescriptor(device, &midiLocation, &buffer);
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc == SFERR_BANK_INDEX_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", bank);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }

    // grab the text from the buffer
    jstring result = (*env)->NewStringUTF(env, desc);

    // done	  
    return result;
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getBankFileName
   * Signature: (II)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getBankFileName(JNIEnv *env, jobject obj, jint device, jint bank) {
	
    // prepare midi descriptor
    CSFMIDILocation midiLocation;
    midiLocation.m_BankIndex = bank;
    midiLocation.m_PresetIndex = 0; // isn't used

    // prepare buffer TODO this is good for a filename with up to 256-2 characters in length
    char file[256];
    
    CSFBufferObject buffer;
    memset(&buffer, 0, sizeof(buffer));
    buffer.m_Size = strlen(file);
    buffer.m_Buffer = file;

    // do the call  
    LRESULT rc = pSFManager101API->SF_GetLoadedBankPathname(device, &midiLocation, &buffer);
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc == SFERR_BANK_INDEX_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", bank);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }
    // since we're polite we're converting backward slashes into forward slashes
    int j=strlen(file);
    int i;
    for (i=0; i<j; i++) {
      if (file[i]=='\\') file[i]='/';
    } 

    // grab the text from the buffer
    jstring result = (*env)->NewStringUTF(env, file);

    // done
    return result;
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    loadBank
   * Signature: (IILjava/lang/String;)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_loadBank(JNIEnv *env, jobject obj, jint device, jint bank, jstring jfile) {

    // convert java file into string
    char* file = (*env)->GetStringUTFChars(env, jfile, NULL);

    // prepare midi descriptor 
    CSFMIDILocation midiLocation;
    midiLocation.m_BankIndex = bank;
    midiLocation.m_PresetIndex = 0; // isn't used
    
    // prepare buffer	  
    CSFBufferObject buffer;
    memset(&buffer, 0, sizeof(buffer));
    buffer.m_Size = strlen(file);
    buffer.m_Flag = SFFLAG_OPER_FILE;
    buffer.m_Buffer = file;
	  
    // do the call  
    LRESULT rc = pSFManager101API->SF_LoadBank(device, &midiLocation, &buffer);

    // check result
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc == SFERR_BANK_INDEX_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", bank);
    } else if (rc == SFERR_DEVICE_BUSY) {
      throwException(env, "java/io/IOException", "device busy");
    } else if (rc == SFERR_PATHNAME_INVALID || rc == SFERR_FORMAT_INVALID) {
      throwException(env, "java/io/FileNotFoundException", "%s", file);
    } else if (rc == SFERR_SYSMEM_INSUFFICIENT || rc == SFERR_SOUNDMEM_INSUFFICIENT) {
      throwException(env, "java/io/IOException", "insufficient memory");
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }

    // release string
    (*env)->ReleaseStringUTFChars(env, jfile, file);    

    // done
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    clearBank
   * Signature: (II)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_clearBank(JNIEnv *env, jobject obj, jint device, jint bank) {
	
    // prepare midi descriptor 
    CSFMIDILocation midiLocation;
    midiLocation.m_BankIndex = bank;
    midiLocation.m_PresetIndex = 0; // isn't used

    // do the call  
    LRESULT rc = pSFManager101API->SF_ClearLoadedBank(device, &midiLocation);

    // check result
    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc == SFERR_BANK_INDEX_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", bank);
    } else if (rc == SFERR_DEVICE_BUSY) {
      throwException(env, "java/io/IOException", "device busy");
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }

    // done
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getPresetDescriptor
   * Signature: (III)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jobject obj, jint device, jint bank, jint program) {

    // prepare midi descriptor
    CSFMIDILocation midiLocation;
    midiLocation.m_BankIndex = bank;
    midiLocation.m_PresetIndex = program;
    
    // prepare buffer TODO good for descriptor up to 256-2 char length
    char desc[256];
    
    CSFBufferObject buffer;
    memset(&buffer, 0, sizeof(buffer));
    buffer.m_Size = strlen(desc);
    buffer.m_Buffer = desc;

    // do the call  
    LRESULT rc = pSFManager101API->SF_GetLoadedPresetDescriptor(device, &midiLocation, &buffer);

    if (rc == SFERR_DEVICE_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid device %d", device);
    } else if (rc == SFERR_BANK_INDEX_INVALID) {
      throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", bank);
    } else if (rc != SFERR_NOERR) {
      throwException(env, "java/lang/Error", "rc %d", rc);
    }

    // grab the text from the buffer
    jstring result = (*env)->NewStringUTF(env, desc);

    // done
    return result;
  }
