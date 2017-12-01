#!/usr/bin/env python
from __future__ import print_function

import sys, gym

from gym import wrappers

import argparse
import logging
import sys
import random

import numpy as np
from asn1crypto._ffi import null


feedback = []
controls = []
#
# Test yourself as a learning agent! Pass environment name as a command-line argument.
#

#env = gym.make('LunarLander-v2' if len(sys.argv)<2 else sys.argv[1])


#may need this if we have to call the jLOAF module directly
sys.path.append("/home/chad/github/NMAI/jLOAF-OpenAI/bin")

class GymEnv(object):

    #env, state, info, reward, done

    def testCommand(self, int_value=None, string_value=None):
        print("Test COmmand: "+int_value, string_value)
        return "Sent command: {0}".format(string_value)


    def getInfo(self, int_value=None, string_value=None):
            print(int_value, string_value)
            env = gym.make('CartPole-v0' if len(sys.argv)<2 else sys.argv[1])
            return "Sent command: {0}".format(string_value)
    
    def makeEnv(self, string_value=None):
        
        print("Making Environment: "+string_value)
        
        if (string_value ):
            env = gym.make(string_value)

    def resetEnv(self):
        
        print("Reset Environment")
        state = env.reset()

    def isDone(self):
        
        print("Asking if Done")
        return done
    
    def doAction(self, action=None):
        
        print("Doing Action")
        
        state, reward, done, info = env.step(action)
        return state

            
    class Java:
        implements = ["Environment.GymEnv"]


print ("-- Opening GymDoor Server -- ")

gym_env = GymEnv()

# Make sure that the python code is started first.
# Then execute: java -cp py4j.jar py4j.examples.SingleThreadClientApplication
from py4j.clientserver import ClientServer, JavaParameters, PythonParameters

gateway = ClientServer(
    java_parameters=JavaParameters(),
    python_parameters=PythonParameters(),
    python_server_entry_point=gym_env)



#from py4j.java_gateway import JavaGateway //Old Client/Server model
#gateway = JavaGateway()
#jLoaf = gateway.jvm.Environment.JloafClient()





#print ("-- Training Agent --")
#jLoaf.trainAgent()

#gateway.jvm.java.lang.System.out.println('Hello JVM!!')
