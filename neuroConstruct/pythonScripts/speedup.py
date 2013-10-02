# Some results of a speed test done on legion. Granule layer simulation (de Schutter 98) 
# run for 30ms with:
# 10 Golgi cells
# 1450 Mossy fibres
# 4174 Granule cells
# in a volume of (130microns)**3 (realistic densities)
# Guy Billings 06.04.09

import matplotlib.pyplot as plt
import numpy

times_s=numpy.zeros(8)
times_s[0]=239.5
times_s[1]=47.8
times_s[2]=21.0
times_s[3]=8.8
times_s[4]=6.0
times_s[5]=4.7
times_s[6]=3.9
times_s[7]=3.6

procs=numpy.zeros(8)
procs[0]=1
procs[1]=4
procs[2]=8
procs[3]=16
procs[4]=24
procs[5]=32
procs[6]=40
procs[7]=48

plt.loglog(procs, times_s, 'ro-')

plt.ylabel('Simulation time (s)')
plt.xlabel('Number of processors')


plt.show()