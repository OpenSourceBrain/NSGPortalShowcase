import sys

print("Hello from Python: %s"% sys.version)



print('\n========================================================\n')
import pyNN

print(">> PyNN: version %s"%pyNN.__version__)

print('\n========================================================\n')



import neuron

print(">> NEURON: version %s"%neuron.h.nrnversion())

print('\n========================================================\n')




try:
    import nest
    print(">> NEST: version %s"%nest.version())

except:
    print("No PyNEST...")

print('\n========================================================\n')




try:
    import brian

    print(">> Brian: version %s"%brian.__version__)
    
except:
    print("No Brian v1...")

print('\n========================================================\n')



try:
    import brian2

    print(">> Brian2: version %s"%brian2.__version__)

except:
    print("No Brian v1...")

print('\n========================================================\n')



import netpyne

print(">> NetPyNE: version %s"%netpyne.__version__)
print('\n========================================================\n')
