#!/usr/bin/env python
from __future__ import print_function

import sys, gym

from gym import wrappers

from py4j.clientserver import ClientServer, JavaParameters, PythonParameters

from py4j.java_gateway import JavaGateway


import argparse
import logging
import sys
import random

import numpy as np
#from asn1crypto._ffi import null


feedback = []
controls = []

DEBUG_MSG = False
DEBUG_ACTION = False



env = 0
state = 0
reward = 0
done = False
info = 0
ob_space = 4
action_space = 4


#
# Test yourself as a learning agent! Pass environment name as a command-line argument.
#

#env = gym.make('LunarLander-v2' if len(sys.argv)<2 else sys.argv[1])


#may need this if we have to call the jLOAF module directly
sys.path.append("/home/chad/github/NMAI/jLOAF-OpenAI/bin")

class globals():
    env = 0
    state = 0
    reward = 0
    done = False
    info = 0
    ob_space = 4
    action_space = 4    

class GymEnv(object):

    
    def testCommand(self, int_value=None, string_value=None):
        print(int_value, string_value)
        return "TestCommand: {0} {1}".format(int_value, string_value)

    def getInfo(self, int_value=None, string_value=None):
            print(int_value, string_value)
            globals.env = gym.make('CartPole-v0' if len(sys.argv)<2 else sys.argv[1])
            return "Sent command: {0}".format(string_value)
    
    def makeEnv(self, environment=None):
        
        print ("Command: makeEnv" + " Value: {0}".format(environment))
        
        if (environment):
            globals.env = gym.make(environment)
            globals.env.reset();
            #globals.ob_space = globals.env.observation_space
            
        return "Making Environment: {0}".format(environment)

    def resetEnv(self):
        
        print("Reset Environment")
        globals.state = globals.env.reset()
        #globals.done = False
        return ("Reset Environment")

    def isDone(self):
        
        if(DEBUG_MSG) : print("Asking if Done")
        return globals.done
    
    def doAction(self, action=None):
        
        if(DEBUG_ACTION): print("Do Action: {0}".format(action))
        

        
        #action = 1
        state, \
        globals.reward, \
        globals.done, \
        globals.info = globals.env.step(action)
        
        #globals.env.step(action)
        globals.env.render()
        
        state_size = state.size
        
        state_string = ','.join(['%.2f' % num for num in state])
        
        return state_string
            
    class Java:
        implements = ["Environment.GymEnv"]


print ("-- Opening GymDoor Server -- ")

gym_env = GymEnv()

done = False


gateway = ClientServer(
    java_parameters=JavaParameters(),
    python_parameters=PythonParameters(),
    python_server_entry_point=gym_env)

# Make sure that the python code is started first.
# Then execute: java -cp py4j.jar py4j.examples.SingleThreadClientApplication





#from py4j.java_gateway import JavaGateway //Old Client/Server model
#gateway = JavaGateway()
#jLoaf = gateway.jvm.Environment.JloafClient()





#print ("-- Training Agent --")
#jLoaf.trainAgent()

#gateway.jvm.java.lang.System.out.println('Hello JVM!!')
