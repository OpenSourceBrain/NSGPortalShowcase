import sys

print("Hello from Python: %s"% sys.version)


print('\n========================================================\n')
try:
    import pyNN
    print(">> PyNN: version %s"%pyNN.__version__)
except:
    print("No PyNN...")


print('\n========================================================\n')
try:
    import neuron
    print(">> NEURON: version %s"%neuron.h.nrnversion())
except:
    print("No NEURON...")



print('\n========================================================\n')
try:
    import neuroml
    print(">> libNeuroML: version %s"%neuroml.__version__)
except Exception as e:
    print("No libNeuroML: %s"%e)


print('\n========================================================\n')
try:
    import pyneuroml
    print(">> pyNeuroML: version %s"%pyneuroml.__version__)
except Exception as e:
    print("No pyNeuroML: %s"%e)


print('\n========================================================\n')
try:
    import netpyne
    print(">> NetPyNE: version %s"%netpyne.__version__)
except Exception as e:
    print("No netpyne: %s"%e)

print('\n========================================================\n')

print('Init for main script...')
import init
