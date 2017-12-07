#
# The following code liberally taken from the OpenAI gym/examples/random_agent.py source
#

import argparse
import logging
import sys
import random

import numpy as np
#import nengo

import gym
from gym import wrappers
from numpy import argmax



def heuristic(observation):

    parameters = np.array([\
                #[ 0.08718784,  0.71794107],
                [ 0.08718784,  -0.71794107],
                [ 0.37608265,  0.66108118],
                [ 0.08718784,  0.71794107]\
                ])
    
    
    actions = np.zeros(3)    
    
    observation[1] *= 2
    
    actions[0] = np.matmul(parameters[0],observation)
    actions[1] = np.matmul(parameters[1],observation)
    actions[2] = np.matmul(parameters[2],observation)
    
    return actions

if __name__=="__main__":

    env = gym.make("MountainCar-v0")

    #print("Action Space:")
    #print(env.action_space)
    
    
    #print("Observation Space:")
    #print(env.observation_space)
    #print(env.observation_space.high)
    #print(env.observation_space.low)
    
    #write some transform to dynamically generate parameter matrix        
    
    reward = 0
    total_reward = 0
    steps = 0


    
    s = env.reset()
    
    while steps < 1000:
        
        action = heuristic(s)
        a = np.argmax(action)
                
        s, r, done, info = env.step(a)
        env.render()
        total_reward += r
        #if steps % 20 == 0 or done:
        
        #output = ','.join(['%.2f' % num for num in s])
        output = ["{:+0.2f}".format(x) for x in s]            
        output.append(a)
        print(output)
        
        #print(["{:+0.2f}".format(x) for x in s]+"{:+0.2f}".format(a))        
        
        #print("step {} total_reward {:+0.2f}".format(steps, total_reward))
        
        steps += 1
        if done: 
            s = env.reset()
