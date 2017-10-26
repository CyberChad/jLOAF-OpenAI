from java.lang import System
from java.util import Vector

import sys
#sys.path.append('/home/chad/jython2.7.0/Lib/') #use this to import Java module
#sys.path.append('/home/chad/jython2.7.0/Lib/dist-packages/') #use this to import Java module

#import numpy

from java.io import FileOutputStream

class UppercaseFileOutputStream(FileOutputStream):
    def write_upper(self, text):
        text = text.upper()
        self.write(text)

def test(outfilename):
    fos = UppercaseFileOutputStream(outfilename)
    for idx in range(10):
        fos.write_upper('This is line # %d\n' % idx)
    fos.close()
    infile = open(outfilename, 'r')
    for line in infile:
        line = line.rstrip()
        print 'Line: %s' % line



def printsys():
    
    r = sys.registry
    for k in r:
        print k, r[k]


def printprops():
    
    props = System.getProperties()
    names = []
    
    for name in props.keys():
        names.append(name)        
    names.sort()
    
    for val in props['java.class.path'].split(':'):
        print val    

def testVector():
    
    v = Vector()
    v.add('aaa')
    v.add('bbb')
    for val in v:
        print val
 
def parseInputs():
    args = sys.argv[1:]
    if len(args) != 1:
        print 'usage: extend_fileoutputstream.py <infilename>'
        sys.exit(1)
    test(args[0])

def main():
    
    print 'Hello World!'
    
    #printsys()
    #printprops()




if __name__ == '__main__':
    #import pdb; pdb.set_trace() #debug using pdb interface
    main()



