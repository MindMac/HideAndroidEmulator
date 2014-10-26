#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
Created on 2014-9-21

@author: MindMac
'''
import subprocess, argparse
import os,sys

from repackage import Repackage

adb_tool_path = r'D:\ProgramTools\SDK\platform-tools\adb.exe'
tool_apks_dir = r'resources/tool_apks'
demo_apks_dir = r'resources/demo_apks'
tools_dir = r'resources/tools'
decompiled_dir = r'decompiled'
repackage_dir = r'repackage'
    
class EmulatorClient:
    def __init__(self, emu_port):
        self.emu_port = emu_port
    
    def get_emulator_state(self):
        """
        Returns emulator state
        """
        state = None
        cmd = ['get-state']
        retval = self.run_adb_cmd(cmd)
        if retval:
            state = retval[0]
            state = state.strip()
        return state
    
    def install_app(self, app):
        """
        Installs the provided app on the emulator
        """
        retval = self.run_adb_cmd(['install', app])
        if retval[0].find('Success') == -1:
            return False
        else:
            return True
            
    def remount(self):
        retval = self.run_adb_cmd(['remount'])
        if retval[0].find('remount succeeded') == -1:
            return False
        else:
            return True
        
    def push_file(self, host_file, client_file):
        cmd = ['push', host_file, client_file]
        retval = self.run_adb_cmd(cmd)

    def change_mode(self, client_file, mode):
        cmd = ['shell', 'chmod', mode, client_file]
        retval = self.run_adb_cmd(cmd)
        
    def run_adb_cmd(self, cmd):
        """
        Runs a simple adb command
        """
        args = [adb_tool_path, '-s', 'emulator-%s' % str(self.emu_port)]
        args.extend(cmd)
        retval = None
        print 'Execute adb command: %s' % ' '.join(args)
        try:
            adb_process = subprocess.Popen(args,stdout=subprocess.PIPE,
                                            stderr=subprocess.PIPE)
            retval = adb_process.communicate()
            print retval
            
        except Exception, ex:
            print 'Failed to run adb command : %s' % ' '.join(args)
              
        adb_process = None
        return retval
    
if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-p', action='store', dest='emu_port')
    #args = parser.parse_args(['-p', '5554'])
    args = parser.parse_args()
    
    emu_port = args.emu_port
    emu_client = EmulatorClient(emu_port)
    
    # Check emulator state
    state = emu_client.get_emulator_state()
    if state != 'device':
        print 'Emulator not online'
        sys.exit(0)
    
    # Remount
    if not emu_client.remount():
        print 'Remount failed'
        sys.exit(0)
    
    
    cur_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Push su
    su_src_file = os.path.join(cur_dir, tools_dir, 'su')
    su_dst_file = '/system/xbin/su'
    emu_client.push_file(su_src_file, su_dst_file)
    
    # Chmod
    emu_client.change_mode('/system/xbin', '06755')
    emu_client.change_mode('/system/xbin/su', '06755')
    
    # Install tool apks
    tool_apks_dir = os.path.join(cur_dir, tool_apks_dir)
    apk_files = os.listdir(tool_apks_dir)
    for apk_file in apk_files:
        apk_file_path = os.path.join(cur_dir, tool_apks_dir, apk_file)
        if not emu_client.install_app(apk_file_path):
            print 'Install %s failed' % apk_file
    
    # Repacakge
    decompiled_dir = os.path.join(cur_dir, decompiled_dir)
    if not os.path.exists(decompiled_dir):
        os.makedirs(decompiled_dir)
        
    repackage_dir = os.path.join(cur_dir, repackage_dir)
    if not os.path.exists(repackage_dir):
        os.makedirs(repackage_dir)
        
    demo_apks_dir = os.path.join(cur_dir, demo_apks_dir)
    apk_files = os.listdir(demo_apks_dir)
    for apk_file in apk_files:
        apk_file_path = os.path.join(cur_dir, demo_apks_dir, apk_file)
        apk_decompiled_dir = os.path.join(decompiled_dir, apk_file)
        repackage_apk = os.path.join(repackage_dir, apk_file)
        repackage = Repackage(apk_file_path, apk_decompiled_dir, repackage_apk)
        repackage.do_repackage()
        
    # Install demo apks
    apk_files = os.listdir(repackage_dir)
    for apk_file in apk_files:
        apk_file_path = os.path.join(repackage_dir, apk_file)
        if not emu_client.install_app(apk_file_path):
            print 'Install %s failed' % apk_file_path
        
        
    
        
        
    
    
     
    


    
