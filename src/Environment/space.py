from py4j.java_gateway import JavaGateway
gateway = JavaGateway()
java_object = gateway.jvm.Experiment.GymSim()
java_object.consoleTest()

gateway.jvm.java.lang.System.out.println('Hello JVM!!')

import sys

sys.path.append("/home/chad/github/NMAI/jLOAF-OpenAI/bin")

#from Experiment import JepGymSim

import gym
env = gym.make('SpaceInvaders-v0')
env.reset()
#env.render(close=False)





#for i in range(0,10000):
    #env.render()
    #env.step(env.action_space.sample())

