#!/usr/bin/env python
from __future__ import print_function

import sys, gym

from gym import wrappers

import argparse
import logging
import sys
import random

import numpy as np


feedback = []
controls = []
#
# Test yourself as a learning agent! Pass environment name as a command-line argument.
#

env = gym.make('LunarLander-v2' if len(sys.argv)<2 else sys.argv[1])

#may need this if we have to call the jLOAF module directly
sys.path.append("/home/chad/github/NMAI/jLOAF-OpenAI/bin")


from py4j.java_gateway import JavaGateway

print ("-- Connecting to JLOAF Server -- ")

gateway = JavaGateway()
jLoaf = gateway.jvm.Experiment.JloafServer()

#print ("-- Training Agent --")
#jLoaf.trainAgent()

#gateway.jvm.java.lang.System.out.println('Hello JVM!!')

parameters = np.array([\
           [ 0.55502267, -0.45502267, -0.66601471, -0.85858478],
           [ -0.55502267, 0.45502267, 0.66601471, 0.85858478],
           #[0.83776774, 0.08762515, 0.10835961, 0.45194732],
           #[-0.83776774, -0.08762515, -0.10835961, -0.45194732]\
           ]) 
   

actions = np.zeros(2) 

next_action = 0


#from Experiment import JepGymSim

import gym
env = gym.make('CartPole-v0')
env.reset()

done = 0

double_class = gateway.jvm.double
double_array = gateway.new_array(double_class, 4)

while not done:
    #i in range(0,10000):
    
    observation, reward, done, info = env.step(next_action)
    
    
    
    print("Sending observation to server")
    

        
    double_array[0] = observation[0]
    double_array[1] = observation[1]    
    double_array[2] = observation[2]    
    double_array[3] = observation[3]
    
    tempaction = jLoaf.nextAction(double_array)
    
    #print("Action from Server: "+tempaction)
    
    actions[0] = np.matmul(parameters[0],observation)
    actions[1] = np.matmul(parameters[1],observation)
    
    next_action = np.argmax(actions) #replace with call to jLoafServer
    
    env.render()
    import time
    time.sleep(0.05)

    #jLoaf.nextAction(obs)