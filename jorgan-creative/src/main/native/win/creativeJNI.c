#include <stdio.h>
#include "exception.h"
#include "jorgan.creative.SoundFontManager.h"
#include "SFMAN.H"

/** One global reference to the Sound Font Manager API */
static PSFMANL101API pSFManager101API = NULL;

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {

	JNIEnv* env;

	if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_2)) {
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

JNIEXPORT
void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
}

typedef struct _Context {
	char* deviceName;
    int deviceIndex;
} Context;

Context* createContext() {
	return (Context*) malloc(sizeof(Context));
}

void destroyContext(Context* context) {
	free(context->deviceName);

	free(context);
}

JNIEXPORT
jobject JNICALL Java_jorgan_creative_SoundFontManager_init(JNIEnv* env, jclass jclass, jstring jdeviceName) {
	Context* context = createContext();

	const char* deviceName = (char*) (*env)->GetStringUTFChars(env, jdeviceName, NULL);
	context->deviceName = (char*)calloc(strlen(deviceName) + 1, sizeof(char));
	strcat(context->deviceName, deviceName);
	(*env)->ReleaseStringUTFChars(env, jdeviceName, deviceName);

	int deviceIndex = -1;
    WORD count = 0;
    LRESULT rc = pSFManager101API->SF_GetNumDevs(&count);
    if (rc != SFERR_NOERR) {
		jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
		return NULL;
    }
	for (int i = 0; i < count; i++) {
		CSFCapsObject caps;
		memset(&caps, 0, sizeof(caps));
		caps.m_SizeOf = sizeof(caps);
	    LRESULT rc = pSFManager101API->SF_GetDevCaps(i, &caps);
	    if (rc != SFERR_NOERR) {
			jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
			return NULL;
	    }

		if (strcmp(context->deviceName, caps.m_DevName) == 0) {
			deviceIndex = i;
			break;
		}
	}
	if (deviceIndex == -1) {
		jorgan_throwException(env, "java/lang/IllegalArgumentException", "no creative device");
		return NULL;
	}
	context->deviceIndex = deviceIndex;

	return (*env)->NewDirectByteBuffer(env, (void*) context, sizeof(Context));
}

JNIEXPORT
void JNICALL Java_jorgan_creative_SoundFontManager_destroy(JNIEnv* env, jclass jclass, jobject jcontext) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	destroyContext(context);
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_clear(JNIEnv* env, jclass jclass, jobject jcontext, jint jbank) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	CSFMIDILocation midiLocation;
	midiLocation.m_BankIndex = jbank;
	midiLocation.m_PresetIndex = 0;

	LRESULT rc = pSFManager101API->SF_ClearLoadedBank(context->deviceIndex, &midiLocation);

	if (rc == SFERR_BANK_INDEX_INVALID) {
		jorgan_throwException(env, "java/io/IOException", "invalid bank");
	} else if (rc == SFERR_DEVICE_BUSY) {
		jorgan_throwException(env, "java/io/IOException", "device busy");
	} else if (rc != SFERR_NOERR) {
		jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
	}
}

JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isLoaded(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	WORD bank = jbank;

	LRESULT rc = pSFManager101API->SF_IsMIDIBankUsed(context->deviceIndex, &bank);
	if (rc == SFERR_BANK_INDEX_INVALID) {
		// signal that is isn't used :(
		// jorgan_throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", jbank);
	} else if (rc != SFERR_NOERR) {
		jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
	}

	return bank == 65535 ? JNI_FALSE : JNI_TRUE; 
}

JNIEXPORT void JNICALL Java_jorgan_creative_SoundFontManager_load(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jstring jfileName) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	const char* fileName = (*env)->GetStringUTFChars(env, jfileName, NULL);

	CSFMIDILocation midiLocation;
	midiLocation.m_BankIndex = jbank;
	midiLocation.m_PresetIndex = 0;

	CSFBufferObject buffer;
	memset(&buffer, 0, sizeof(buffer));
	buffer.m_Size = strlen(fileName);
	buffer.m_Flag = SFFLAG_OPER_FILE;
	buffer.m_Buffer = (char*)fileName;

	LRESULT rc = pSFManager101API->SF_LoadBank(context->deviceIndex, &midiLocation, &buffer);

	if (rc == SFERR_BANK_INDEX_INVALID) {
		jorgan_throwException(env, "java/io/IOException", "invalid bank");
	} else if (rc == SFERR_DEVICE_BUSY) {
		jorgan_throwException(env, "java/io/IOException", "device busy");
	} else if (rc == SFERR_PATHNAME_INVALID || rc == SFERR_FORMAT_INVALID) {
		jorgan_throwException(env, "java/io/FileNotFoundException", "%s", fileName);
	} else if (rc == SFERR_SYSMEM_INSUFFICIENT || rc == SFERR_SOUNDMEM_INSUFFICIENT) {
		jorgan_throwException(env, "java/io/IOException", "insufficient memory");
	} else if (rc != SFERR_NOERR) {
		jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
	}

	(*env)->ReleaseStringUTFChars(env, jfileName, fileName);
}

JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);
	
	CSFMIDILocation midiLocation;
	midiLocation.m_BankIndex = jbank;
	midiLocation.m_PresetIndex = 0; // isn't used

	char desc[256];
	CSFBufferObject buffer;
	memset(&buffer, 0, sizeof(buffer));
	buffer.m_Size = strlen(desc);
	buffer.m_Buffer = desc;

	LRESULT rc = pSFManager101API->SF_GetLoadedBankDescriptor(context->deviceIndex, &midiLocation, &buffer);
	if (rc == SFERR_BANK_INDEX_INVALID) {
		jorgan_throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", jbank);
	} else if (rc != SFERR_NOERR) {
		jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
	}

	return (*env)->NewStringUTF(env, desc);
}
	
JNIEXPORT jstring JNICALL Java_jorgan_creative_SoundFontManager_getPresetDescriptor(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank, jint jpreset) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	char descriptor[256];

	CSFMIDILocation midiLocation;
	midiLocation.m_BankIndex = jbank;
	midiLocation.m_PresetIndex = jpreset;

	CSFBufferObject buffer;
	memset(&buffer, 0, sizeof(buffer));
	buffer.m_Size = strlen(descriptor);
	buffer.m_Buffer = descriptor;

	LRESULT rc = pSFManager101API->SF_GetLoadedPresetDescriptor(context->deviceIndex, &midiLocation, &buffer);

	if (rc == SFERR_BANK_INDEX_INVALID) {
		jorgan_throwException(env, "java/lang/IllegalArgumentException", "invalid bank %d", jbank);
	} else if (rc == SFERR_PRESET_INDEX_INVALID) {
		jorgan_throwException(env, "java/lang/IllegalArgumentException", "invalid preset %d", jpreset);
	} else if (rc != SFERR_NOERR) {
		jorgan_throwException(env, "java/lang/Error", "rc %d", rc);
	}

	return (*env)->NewStringUTF(env, descriptor);
}
