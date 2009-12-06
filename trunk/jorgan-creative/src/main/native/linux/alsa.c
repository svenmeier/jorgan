/*
 * ALSA hwdep interface
 *
 * Copyright (C) 2003 Takashi Iwai
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

#include <stdio.h>
#include <alsa/asoundlib.h>
#include <awe_voice.h>
#include <sys/ioctl.h>

#define SNDRV_EMUX_HWDEP_NAME	"Emux WaveTable"

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



static snd_hwdep_t *hwdep;

static int try_open_emux(char *name)
{
	snd_hwdep_info_t *info;
	unsigned int version;

	if (snd_hwdep_open(&hwdep, name, 0) < 0)
		return -1;
	snd_hwdep_info_alloca(&info);
	if (snd_hwdep_info(hwdep, info) < 0)
		goto error;
	if (strcmp(snd_hwdep_info_get_name(info), SNDRV_EMUX_HWDEP_NAME))
		goto error;
	if (snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_VERSION, &version) < 0)
		goto error;
	if ((version >> 16) != 0x01) /* version 1 compatible */
		goto error;
	return 0;

 error:
	snd_hwdep_close(hwdep);
	hwdep = NULL;
	return -1;
}

void seq_alsa_init(char *name)
{
	char tmpname[32];
	int i;

	if (name == NULL || ! *name) {
		for (i = 0; i < 8; i++) {
			sprintf(tmpname, "hw:%d,2", i);
			if (try_open_emux(tmpname) == 0)
				return;
		}
	} else {
		if (try_open_emux(name) == 0)
			return;
	}
	fprintf(stderr, "No Emux synth hwdep device is found\n");
	exit(1);
}

void seq_alsa_end(void)
{
	if (hwdep) {
		snd_hwdep_close(hwdep);
		hwdep = NULL;
	}
}

int seq_reset_samples(void)
{
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_RESET_SAMPLES, NULL);
}

int seq_remove_samples(void)
{
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_REMOVE_LAST_SAMPLES, NULL);
}

int seq_load_patch(void *patch, int len)
{
 	awe_patch_info *p;
 	p = (awe_patch_info*)patch;
 	p->key = AWE_PATCH;
	p->device_no = 0;
 	p->sf_id = 0;
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_LOAD_PATCH, p);
}

int seq_mem_avail(void)
{
	int mem_avail = 0;
	snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_MEM_AVAIL, &mem_avail);
	return mem_avail;
}

int seq_zero_atten(int atten)
{
	struct sndrv_emux_misc_mode mode;
	mode.port = -1;
	mode.mode = AWE_MD_ZERO_ATTEN;
	mode.value = atten;
	mode.value2 = 0;
	return snd_hwdep_ioctl(hwdep, SNDRV_EMUX_IOCTL_MISC_MODE, &mode);
}
