#!/usr/bin/python
# -*- coding: utf-8 -*-

'''
Created on 2014-4-9

@author: Wenjun Hu
'''

# Global import
import os, subprocess, shutil

APKTOOL = r'resources/tools/apktool.jar'
SIGN_KEY = r'resources/tools/sign_key'
BUILD_SMALI = 'resources/tools/Build.smali'
SIGN_KEY_PASS = 'mindmac'
SIGN_KEY_ALIAS = 'MindMac'

BUILD_LIST = ['Landroid/os/Build;->ID',
              'Landroid/os/Build;->DISPLAY',
              'Landroid/os/Build;->PRODUCT',
              'Landroid/os/Build;->DEVICE',
              'Landroid/os/Build;->BOARD',
              'Landroid/os/Build;->MANUFACTURER',
              'Landroid/os/Build;->BRAND',
              'Landroid/os/Build;->MODEL',
              'Landroid/os/Build;->BOOTLOADER',
              'Landroid/os/Build;->TAGS',
              'Landroid/os/Build;->FINGERPRINT',
              'Landroid/os/Build;->HARDWARE']

class Repackage:
    def __init__(self, apk_path, apk_decompiled_dir, apk_repackaged_path):
        self.cur_dir = os.path.dirname(os.path.abspath(__file__))
        self.apk_path = apk_path
        self.apk_decompiled_dir = apk_decompiled_dir
        self.apk_repackaged_path = apk_repackaged_path
        
        
    def do_repackage(self):
        self.__decompile()
        self.__scan_build_prop()
        self.__repackage()
        self.__sign()
            
    def __scan_build_prop(self):
        is_build_prop = False
        smali_dir = os.path.join(self.apk_decompiled_dir, 'smali')
        
        if os.path.exists(smali_dir):
            for root, dir, smali_files in os.walk(smali_dir):
                for smali_file in smali_files:
                    ori_smali_path = os.path.join(root, smali_file)
                    ori_smali_obj = open(ori_smali_path, 'r')
                    
                    modified_smali_path = os.path.join(root, '%s.new' % smali_file)
                    modified_smali_obj = open(modified_smali_path, 'w')
                    
                    line = ori_smali_obj.readline()
                    while line:
                        new_line = line.strip()
                        if new_line.startswith('sget-object'):
                            for build_str in BUILD_LIST:
                                if build_str in new_line:
                                    is_build_prop = True
                                    line = line.replace('Landroid/os', 'Lbndroid/os')
                                    break
                        modified_smali_obj.write(line)
                        line = ori_smali_obj.readline()
                    ori_smali_obj.close()
                    modified_smali_obj.close()
                    
                    os.remove(ori_smali_path)
                    os.rename(modified_smali_path, ori_smali_path)
                    
            if is_build_prop:
                build_smali_dir = os.path.join(self.apk_decompiled_dir, 'smali', 
                                               'bndroid', 'os')
                if not os.path.exists(build_smali_dir):
                    os.makedirs(build_smali_dir)
                build_smali_file = os.path.join(self.cur_dir, BUILD_SMALI)
                build_smali_dst_file = os.path.join(build_smali_dir, 'Build.smali')
                shutil.copy(build_smali_file, build_smali_dst_file)
                                    
    def __decompile(self):
        try:
            apktool = os.path.join(self.cur_dir, APKTOOL)
            apktool_args = ['java', '-jar', apktool, 'd', '-r', '-f', '-o', self.apk_decompiled_dir, self.apk_path]
            apktool_popen = subprocess.Popen(apktool_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            retval = apktool_popen.communicate()
            print retval
        except Exception, ex:
            print ex
        
    def __repackage(self):
        try:
            apktool = os.path.join(self.cur_dir, APKTOOL)
            apktool_args = ['java', '-jar', apktool, 'b', '-f', self.apk_decompiled_dir, '-o', self.apk_repackaged_path]
            apktool_popen = subprocess.Popen(apktool_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            retval = apktool_popen.communicate()
        except Exception, ex:
            print ex
            
    def __sign(self):
        try:
            if os.path.exists(self.apk_repackaged_path):
                sign_key_path = os.path.join(self.cur_dir, SIGN_KEY)
                jarsigner_args = ['jarsigner',
                                  '-sigalg',
                                  'MD5withRSA',
                                  '-digestalg',
                                  'SHA1',
                                  '-storepass',
                                  SIGN_KEY_PASS,
                                  '-keystore',
                                  sign_key_path,
                                  self.apk_repackaged_path,
                                  SIGN_KEY_ALIAS,
                                  '-signedjar',
                                  self.apk_repackaged_path
                                  ]
                jarsigner_popen = subprocess.Popen(jarsigner_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                retval = jarsigner_popen.communicate()
        except Exception, ex:
            print ex
