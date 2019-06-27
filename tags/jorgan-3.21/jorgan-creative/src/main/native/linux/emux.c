#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <alsa/asoundlib.h>
#include <awebank.h>
#include <sfopts.h>
#include <awe_voice.h>
#include <sys/ioctl.h>

#define SNDRV_EMUX_HWDEP_NAME "Emux WaveTable"

struct sndrv_emux_misc_mode {
	int port;	/* -1 = all */
	int mode;
	int value;
	int value2;	/* reserved */
};

enum {
	SNDRV_EMUX_IOCTL_VERSION = _IOR('H', 0x80, unsigned int),
	SNDRV_EMUX_IOCTL_LOAD_PATCH = _IOWR('H', 0x81, awe_patch_info),
	SNDRV_EMUX_IOCTL_RESET_SAMPLES = _IO('H', 0x82),
	SNDRV_EMUX_IOCTL_REMOVE_LAST_SAMPLES = _IO('H', 0x83),
	SNDRV_EMUX_IOCTL_MEM_AVAIL = _IOW('H', 0x84, int),
	SNDRV_EMUX_IOCTL_MISC_MODE = _IOWR('H', 0x84, struct sndrv_emux_misc_mode),
};

static snd_hwdep_t* hwdep;

void close_emux()
{
	if (hwdep) {
		snd_hwdep_close(hwdep);
		hwdep = NULL;
	}
}

int open_emux(char *name)
{
	snd_hwdep_info_t* info;
	unsigned int version;

	if (snd_hwdep_open(&hwdep, name, 0) < 0) {
		return -1;
	}

	snd_hwdep_info_alloca(&info);
	if (snd_hwdep_info(hwdep, info) < 0) {
		close_emux();
		return -1;
	}
	if (strcmp(snd_hwdep_info_get_name(info), SNDRV_EMUX_HWDEP_NAME)) {
		close_emux();
		return -1;
	}
	if (snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_VERSION, &version) < 0) {
		close_emux();
		return -1;
	}
	if ((version >> 16) != 0x01) {
		/* version 1 compatible */
		close_emux();
		return -1;
	}

	return 0;
}

static int seq_load_patch(void *patch, int len) {
 	awe_patch_info *p;
 	p = (awe_patch_info*)patch;
 	p->key = AWE_PATCH;
	p->device_no = 0;
 	p->sf_id = 0;
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_LOAD_PATCH, p);
}

static int seq_mem_avail(void)
{
	int mem_avail = 0;
	snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_MEM_AVAIL, &mem_avail);
	return mem_avail;
}

static int seq_reset_samples(void)
{
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_RESET_SAMPLES, NULL);
}

static int seq_remove_samples(void)
{
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_REMOVE_LAST_SAMPLES, NULL);
}

static int seq_zero_atten(int atten)
{
	struct sndrv_emux_misc_mode mode;
	mode.port = -1;
	mode.mode = AWE_MD_ZERO_ATTEN;
	mode.value = atten;
	mode.value2 = 0;
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_MISC_MODE, &mode);
}

static AWEOps load_ops = {
	seq_load_patch,
	seq_mem_avail,
	seq_reset_samples,
	seq_remove_samples,
	seq_zero_atten
};

int emux_remove_samples(int bank)
{
	awe_option.default_bank = bank;

	return seq_remove_samples();
}

void emux_load_samples(int bank, char* fileName) {
	awe_option.default_bank = bank;

	awe_load_bank(&load_ops, fileName, NULL, 0);
}
