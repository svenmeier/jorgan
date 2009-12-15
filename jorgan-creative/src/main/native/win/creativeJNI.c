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

static Context* createContext() {
	Context* context = (Context*) malloc(sizeof(Context));
	context->deviceName = NULL;
	context->deviceIndex = 0;
	return context;
}

static void destroyContext(Context* context) {
	if (context->deviceName != NULL) {
		free(context->deviceName);
	}
	free(context);
}

JNIEXPORT
jobject JNICALL Java_jorgan_creative_SoundFontManager_init(JNIEnv* env, jclass jclass, jstring jdeviceName) {
	Context* context = createContext();

	const char* deviceName = (char*) (*env)->GetStringUTFChars(env, jdeviceName, NULL);
	context->deviceName = (char*)calloc(strlen(deviceName) + 1, sizeof(char));
	strcpy(context->deviceName, deviceName);
	(*env)->ReleaseStringUTFChars(env, jdeviceName, deviceName);

	int deviceIndex = -1;
    WORD count = 0;
    LRESULT rc = pSFManager101API->SF_GetNumDevs(&count);
    if (rc != SFERR_NOERR) {
		destroyContext(context);
		jorgan_throw(env, ERROR, "rc %d", rc);
		return NULL;
    }
	for (int i = 0; i < count; i++) {
		CSFCapsObject caps;
		memset(&caps, 0, sizeof(caps));
		caps.m_SizeOf = sizeof(caps);
	    LRESULT rc = pSFManager101API->SF_GetDevCaps(i, &caps);
	    if (rc != SFERR_NOERR) {
			destroyContext(context);
			jorgan_throw(env, ERROR, "rc %d", rc);
			return NULL;
	    }

		if (strcmp(context->deviceName, caps.m_DevName) == 0) {
			deviceIndex = i;
			break;
		}
	}
	if (deviceIndex == -1) {
		destroyContext(context);
		jorgan_throw(env, IO_EXCEPTION, "no creative device");
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
		jorgan_throw(env, IO_EXCEPTION, "invalid bank");
		return;
	} else if (rc == SFERR_DEVICE_BUSY) {
		jorgan_throw(env, IO_EXCEPTION, "device busy");
		return;
	} else if (rc != SFERR_NOERR) {
		jorgan_throw(env, ERROR, "rc %d", rc);
		return;
	}
}

JNIEXPORT jboolean JNICALL Java_jorgan_creative_SoundFontManager_isLoaded(JNIEnv *env, jclass jclass, jobject jcontext, jint jbank) {
	Context* context = (Context*) (*env)->GetDirectBufferAddress(env, jcontext);

	WORD bank = jbank;

	LRESULT rc = pSFManager101API->SF_IsMIDIBankUsed(context->deviceIndex, &bank);
	if (rc == SFERR_BANK_INDEX_INVALID) {
		// signal that it isn't used :(
		// jorgan_throw(env, "java/lang/IllegalArgumentException", "invalid bank %d", jbank);
	} else if (rc != SFERR_NOERR) {
		jorgan_throw(env, ERROR, "rc %d", rc);
		return;
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
	(*env)->ReleaseStringUTFChars(env, jfileName, fileName);

	if (rc == SFERR_BANK_INDEX_INVALID) {
		jorgan_throw(env, IO_EXCEPTION, "invalid bank");
		return;
	} else if (rc == SFERR_DEVICE_BUSY) {
		jorgan_throw(env, IO_EXCEPTION, "device busy");
		return;
	} else if (rc == SFERR_FORMAT_INVALID) {
		jorgan_throw(env, IO_EXCEPTION, "rc %d", rc);
		return;
	} else if (rc == SFERR_PATHNAME_INVALID) {
		jorgan_throw(env, FILE_NOT_FOUND_EXCEPTION, "rc %d", rc);
		return;
	} else if (rc == SFERR_SYSMEM_INSUFFICIENT || rc == SFERR_SOUNDMEM_INSUFFICIENT) {
		jorgan_throw(env, IO_EXCEPTION, "insufficient memory");
		return;
	} else if (rc != SFERR_NOERR) {
		jorgan_throw(env, ERROR, "rc %d", rc);
		return;
	}
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
		jorgan_throw(env, ILLEGAL_ARGUMENT_EXCEPTION, "invalid bank %d", jbank);
		return NULL;
	} else if (rc != SFERR_NOERR) {
		jorgan_throw(env, ERROR, "rc %d", rc);
		return NULL;
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
		jorgan_throw(env, ILLEGAL_ARGUMENT_EXCEPTION, "invalid bank %d", jbank);
		return NULL;
	} else if (rc == SFERR_PRESET_INDEX_INVALID) {
		jorgan_throw(env, ILLEGAL_ARGUMENT_EXCEPTION, "invalid preset %d", jpreset);
		return NULL;
	} else if (rc != SFERR_NOERR) {
		jorgan_throw(env, ERROR, "rc %d", rc);
		return NULL;
	}

	return (*env)->NewStringUTF(env, descriptor);
}
