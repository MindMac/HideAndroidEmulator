/*
 *  Collin's Binary Instrumentation Tool/Framework for Android
 *  Collin Mulliner <collin[at]mulliner.org>
 *  http://www.mulliner.org/android/
 *
 *  (c) 2012,2013
 *
 *  License: LGPL v2.1
 *
 */

#define _GNU_SOURCE
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/select.h>
#include <string.h>
#include <termios.h>
#include <pthread.h>
#include <sys/epoll.h>

#include <jni.h>
#include <stdlib.h>

#include "../base/hook.h"
#include "../base/base.h"

#undef log

#define log(...) \
        {FILE *fp = fopen("/data/local/tmp/hitcon.log", "a+");\
        fprintf(fp, __VA_ARGS__);\
        fclose(fp);}


// this file is going to be compiled into a thumb mode binary

void __attribute__ ((constructor)) my_init(void);

static struct hook_t eph;

// for demo code only
static int counter;

// arm version of hook
extern int my_system_property_get_arm(const char *name, char *value);

/*  
 *  log function to pass to the hooking library to implement central loggin
 *
 *  see: set_logfunction() in base.h
 */
static void my_log(char *msg)
{
	log(msg)
}

int my_system_property_get(const char *name, char *value)
{

	if(strcmp(name, "ro.product.name") == 0){
		log("Key: %s\n", name);
		value = "google";
	}
	return 6;
}

void my_init(void)
{
	counter = 3;

	set_logfunction(my_log);

	hook(&eph, getpid(), "libc.", "__system_property_get", my_system_property_get_arm, my_system_property_get);
}

