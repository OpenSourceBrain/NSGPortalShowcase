'''
Neuron simulator export for:

Components:
    RS (Type: izhikevich2007Cell:  v0=-0.06 (SI voltage) k=7.0E-7 (SI conductance_per_voltage) vr=-0.06 (SI voltage) vt=-0.04 (SI voltage) vpeak=0.035 (SI voltage) a=30.0 (SI per_time) b=-2.0E-9 (SI conductance) c=-0.05 (SI voltage) d=1.0E-10 (SI current) C=1.0E-10 (SI capacitance))
    RS_Iext (Type: pulseGenerator:  delay=0.0 (SI time) duration=0.52 (SI time) amplitude=1.0E-10 (SI current))
    net1 (Type: network)
    sim1 (Type: Simulation:  length=0.52 (SI time) step=2.5E-6 (SI time))


    This NEURON file has been generated by org.neuroml.export (see https://github.com/NeuroML/org.neuroml.export)
         org.neuroml.export  v1.4.3
         org.neuroml.model   v1.4.3
         jLEMS               v0.9.8.0

'''

import neuron

import time
h = neuron.h
h.load_file("stdlib.hoc")

h.load_file("stdgui.hoc")

h("objref p")
h("p = new PythonObject()")

# Adding simulation Component(id=sim1 type=Simulation) of network/component: net1 (Type: network)
print("Population RS_pop contains 1 instance(s) of component: RS of type: izhikevich2007Cell")

h(" {n_RS_pop = 1} ")
'''
Population RS_pop contains instances of Component(id=RS type=izhikevich2007Cell)
whose dynamics will be implemented as a mechanism (RS) in a mod file
'''
h(" create RS_pop[1]")
h(" objectvar m_RS_RS_pop[1] ")

for i in range(int(h.n_RS_pop)):
    h.RS_pop[i].L = 10.0
    h.RS_pop[i](0.5).diam = 10.0
    h.RS_pop[i](0.5).cm = 31.830988618379067
    h.RS_pop[i].push()
    h(" RS_pop[%i]  { m_RS_RS_pop[%i] = new RS(0.5) } "%(i,i))

    h.m_RS_RS_pop[i].v0 = -60.0
    h.m_RS_RS_pop[i].k = 7.0E-4
    h.m_RS_RS_pop[i].vr = -60.0
    h.m_RS_RS_pop[i].vt = -40.0
    h.m_RS_RS_pop[i].vpeak = 35.0
    h.m_RS_RS_pop[i].a = 0.030000001
    h.m_RS_RS_pop[i].b = -0.0019999999
    h.m_RS_RS_pop[i].c = -50.0
    h.m_RS_RS_pop[i].d = 0.1
    h.m_RS_RS_pop[i].C = 1.00000005E-4
    h.pop_section()
# Adding input: Component(id=null type=explicitInput)

h("objectvar explicitInput_RS_Iext_RS_pop_0_RS_pop0")
h("RS_pop[0] { explicitInput_RS_Iext_RS_pop_0_RS_pop0 = new RS_Iext(0.5) } ")

trec = h.Vector()
trec.record(h._ref_t)

h.tstop = 520

h.dt = 0.0025

h.steps_per_ms = 400.0



# File to save: time
# Column: time
h(' objectvar v_time ')
h(' { v_time = new Vector() } ')
h(' v_time.record(&t) ')
h.v_time.resize((h.tstop * h.steps_per_ms) + 1)

# File to save: of0
# Column: RS_pop[0]/v
h(' objectvar v_v_of0 ')
h(' { v_v_of0 = new Vector() } ')
h(' v_v_of0.record(&RS_pop[0].v(0.5)) ')
h.v_v_of0.resize((h.tstop * h.steps_per_ms) + 1)
# Column: RS_pop[0]/u
h(' objectvar v_u_of0 ')
h(' { v_u_of0 = new Vector() } ')
h(' v_u_of0.record(&m_RS_RS_pop[0].u) ')
h.v_u_of0.resize((h.tstop * h.steps_per_ms) + 1)



sim_start = time.time()
print("Running a simulation of %sms (dt = %sms)" % (h.tstop, h.dt))

h.run()

sim_end = time.time()
sim_time = sim_end - sim_start
print("Finished simulation in %f seconds (%f mins), saving results..."%(sim_time, sim_time/60.0))


# File to save: time
f_time_f2 = open('time.dat', 'w')
for i in range(int(h.tstop * h.steps_per_ms) + 1):
    f_time_f2.write('%f'% (float(h.v_time.get(i))/1000.0))  # Save in SI units...
    f_time_f2.write("\n")
f_time_f2.close()
print("Saved data to: time.dat")

# File to save: of0
f_of0_f2 = open('RS_One.dat', 'w')
for i in range(int(h.tstop * h.steps_per_ms) + 1):
    f_of0_f2.write('%e\t'% (float(h.v_time.get(i))/1000.0)) # Time in first column, save in SI units...
    f_of0_f2.write('%e\t'%(float(h.v_v_of0.get(i)) / 1000.0)) # Saving as SI, variable has dim: voltage
    f_of0_f2.write('%e\t'%(float(h.v_u_of0.get(i)) / 1.0E9)) # Saving as SI, variable has dim: current
    f_of0_f2.write("\n")
f_of0_f2.close()
print("Saved data to: RS_One.dat")

save_end = time.time()
save_time = save_end - sim_end
print("Finished saving results in %f seconds"%(save_time))

print("Done")

quit()