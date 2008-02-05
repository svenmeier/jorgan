#include <jni.h>
#include <stdio.h>
#include "SoundFontManager.h"
#include "SFMAN.H"

#ifdef __LCC__

 /**
  * This is the standart implementation of Java 2 OnLoad and OnUnload native
  * library calls. This template defines them empty functions
  */
 JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) { 
   return JNI_VERSION_1_2; 
 }

 JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
 }

#endif

#define IOEX "java/io/IOException"
#define ILLEGALARGEX "java/lang/IllegalArgumentException"

  /** One global reference to the Sound Font Manager API */
  PSFMANL101API pSFManager101API = 0;
  
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

    // create the IOException  
    jclass ioex = (*env)->FindClass(env, exception);
    (*env)->ThrowNew(env, ioex, msg);
    
    // report FALSE
    return FALSE;
  }

  /**
   * Lazy init
   * @return TRUE if initialized, FALSE otherwise
   */
  int lazyInit(JNIEnv* env) {

    // pointer to API already known?
    if (pSFManager101API!=NULL)
      return TRUE; 

    // Load Sound Font Manager DLL
    HANDLE handleSFMAN32 = LoadLibrary(SF_MASTER_MANAGER_FILENAME) ;
    if (handleSFMAN32==NULL) 
      return throwException(env, IOEX, "Couldn't load dynamic library %s", SF_MASTER_MANAGER_FILENAME);
 
    // Lookup function table
    PSFMANAGER pSFManager = (PSFMANAGER)GetProcAddress(handleSFMAN32, SF_FUNCTION_TABLE_NAME);
    if (pSFManager==NULL ) 
      return throwException(env, IOEX, "Couldn't access Sound Font Manager Function Table in library %s", SF_MASTER_MANAGER_FILENAME);
	 
    LRESULT rc = pSFManager->SF_QueryInterface(ID_SFMANL101API, &pSFManager101API );
    if (rc!=SFERR_NOERR) 
      return throwException(env, IOEX, "Couldn't access Sound Font Manager Interface Version %#x, error %d", ID_SFMANL101API, rc);
      
    // we're good
    return TRUE;
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getNumDevices
   * Signature: ()I
   */
  JNIEXPORT jint JNICALL Java_jorgan_creative_SoundFontManager_getNumDevices (JNIEnv *env, jobject obj) {
	
    // do the lazy init
    if (!lazyInit(env))
      return 0;

    // ask for it
    WORD num = 0;
    pSFManager101API->SF_GetNumDevs(&num);
    
    // done
    return num;
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getDeviceName
   * Signature: (I)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDeviceName(JNIEnv *env, jobject obj, jint device) {

    // do the lazy init
    if (!lazyInit(env))
      return NULL;

    // prepare result data structure	    
    CSFCapsObject caps;
    memset(&caps, 0, sizeof(caps));
    caps.m_SizeOf = sizeof(caps);

    // ask for the data
    pSFManager101API->SF_GetDevCaps(device, &caps);

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

    // do the lazy init
    if (!lazyInit(env))
      return;
    
    // open the device
    LRESULT rc = pSFManager101API->SF_Open(device);
    if (rc!=SFERR_NOERR) {
      throwException(env, IOEX, "Couldn't open device %d, error %d", device, rc);
      return;
    }
	  
    // done
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    close
   * Signature: (I)V
   */
  JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_close(JNIEnv *env, jobject obj, jint device) {

    // do the lazy init
    if (!lazyInit(env))
      return;
    
    // close it
    LRESULT rc = pSFManager101API->SF_Close(device);
    if (rc!=SFERR_NOERR) {
      throwException(env, IOEX, "Couldn't close device %d, error %d", device, rc);
      return;
    }
	  
    // done	  
  }
	
  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    isBankUsed
   * Signature: (II)Z
   */
  JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isBankUsed(JNIEnv *env, jobject obj, jint device, jint bank) {

    // do the lazy init
    if (!lazyInit(env))
      return JNI_FALSE;
    
    // ask manager - after the call:
    // if bank2result ... then bank contains ...
    //   =bank | loaded presets or waveforms
    //  !=bank | loaded presets or waveforms loaded form another bank
    //   -1    | no loaded presets or waveforms      

    WORD bank2result = bank;

    pSFManager101API->SF_IsMIDIBankUsed(device, &bank2result);

    return bank2result==65535 ? JNI_FALSE : JNI_TRUE; 
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getBankDescriptor
   * Signature: (II)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getBankDescriptor(JNIEnv *env, jobject obj, jint device, jint bank) {
	
    // do the lazy init
    if (!lazyInit(env))
      return NULL;
    
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
    if (rc!=SFERR_NOERR) {
      throwException(env, IOEX, "Couldn't get bank descriptor for bank %d, error %d", bank, rc);
      return NULL;
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
	
    // do the lazy init
    if (!lazyInit(env))
      return NULL;
    
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
    if (rc!=SFERR_NOERR) 
      throwException(env, IOEX, "Couldn't get filename of bank %d, error %d", bank, rc);

    // since we're polite we're converting backward slashes into forward slashes
    for (int i=0, j=strlen(file); i<j; i++) {
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

    // do the lazy init
    if (!lazyInit(env))
      return;
    
    // convert java file into string
    char* file = (*env)->GetStringUTFChars(env, jfile, NULL);

    // since we're paranoid we're converting forward slashes into backward slashes
    // we SHOULD really check isCopy to make sure we're not breaking an invariant ;)
    for (int i=0, j=(*env)->GetStringLength(env, jfile); i<j; i++) {
      if (file[i]=='/') file[i]='\\';
    } 
	  
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
    if (rc!=SFERR_NOERR) 
      throwException(env, IOEX, "Couldn't load file %s int bank %d, error %d", file, bank, rc);

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
	
    // do the lazy init
    if (!lazyInit(env))
      return;
    
    // prepare midi descriptor 
    CSFMIDILocation midiLocation;
    midiLocation.m_BankIndex = bank;
    midiLocation.m_PresetIndex = 0; // isn't used

    // do the call  
    LRESULT rc = pSFManager101API->SF_ClearLoadedBank(device, &midiLocation);

    // check result
    if (rc!=SFERR_NOERR) 
      throwException(env, IOEX, "Couldn't clear bank %d, error %d", bank, rc);

    // done
  }

  /**
   * Class:     jorgan_creative_SoundFontManager
   * Method:    getPresetDescriptor
   * Signature: (III)Ljava/lang/String;
   */
  JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jobject obj, jint device, jint bank, jint program) {

    // do the lazy init
    if (!lazyInit(env))
      return NULL;

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
    if (rc!=SFERR_NOERR) 
      throwException(env, IOEX, "Couldn't get descriptor of bank %d program %d, error %d", bank, program, rc);

    // grab the text from the buffer
    jstring result = (*env)->NewStringUTF(env, desc);

    // done
    return result;
  }
