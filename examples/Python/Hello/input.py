import sys

print("Hello from Python version: %s"% sys.version)


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
    print("No Brian v2...")


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
