import matplotlib.pyplot as plt
from pylab import *

print "Going to plot performance of simulations on multiple machines"

T_time = 1000

times_L = {}

times_L[1] = 1.8823334*60.
times_L[2] = 53.07
times_L[4] = 26.95
times_L[8] = 13.98
times_L[16] = 7.31
times_L[32] = 4.36

for i in times_L.keys():
	times_L[i] = 1000 * times_L[i] / T_time


times_E = {}

times_E[1]=1.5285*60.
times_E[2]=51.31
times_E[3]=44.57
times_E[4]=40.7

for i in times_E.keys():
	times_E[i] = 1000 * times_E[i] / T_time



times_B = {}

times_B[1] = 3.35*60.
times_B[2] = 1.354*60.
times_B[3] = 51.49
times_B[4] = 40.37

for i in times_B.keys():
	times_B[i] = 1000 * times_B[i] / T_time


times_M = {}

times_M[1] = 82.73
times_M[4] = 19.87
times_M[8] = 9.65

for i in times_M.keys():
	times_M[i] = 1000 * times_M[i] / T_time


times_Lm = {}

times_Lm[1] = 107.62
times_Lm[4] = 25.7
times_Lm[8] = 12.62

for i in times_Lm.keys():
	times_Lm[i] = 1000 * times_Lm[i] / T_time




times_L_ideal = {}

for i in times_L.keys():
	proc_norm = min(times_L.keys())
	times_L_ideal[i] = proc_norm * times_L[proc_norm]/i


times_B_ideal = {}

for i in times_B.keys():
	proc_norm = min(times_B.keys())
	times_B_ideal[i] = proc_norm * times_B[proc_norm]/i

times_E_ideal = {}

for i in times_E.keys():
	proc_norm = min(times_E.keys())
	times_E_ideal[i] = proc_norm * times_E[proc_norm]/i

times_M_ideal = {}

for i in times_M.keys():
	proc_norm = min(times_M.keys())
	times_M_ideal[i] = proc_norm * times_M[proc_norm]/i

times_Lm_ideal = {}

for i in times_Lm.keys():
	proc_norm = min(times_Lm.keys())
	times_Lm_ideal[i] = proc_norm * times_Lm[proc_norm]/i
	


def getXvals(times):
    x = times.keys()
    x.sort()
    return x

def getYvals(times):
    x = times.keys()
    x.sort()
    y = []
    for t in x:
        y.append(times[t])
    return y


from matplotlib.backends.backend_agg import FigureCanvasAgg as FigureCanvas


fig = plt.figure()

p = fig.add_subplot(111)

lines = p.loglog(getXvals(times_L), getYvals(times_L), 'ro-', getXvals(times_L_ideal), getYvals(times_L_ideal), 'r:', \
		 getXvals(times_E), getYvals(times_E), 'bo-', getXvals(times_E_ideal), getYvals(times_E_ideal), 'b:', \
		 getXvals(times_B), getYvals(times_B), 'go-', getXvals(times_B_ideal), getYvals(times_B_ideal), 'g:', \
		 getXvals(times_M), getYvals(times_M), 'ko-', getXvals(times_M_ideal), getYvals(times_M_ideal), 'k:', \
		 getXvals(times_Lm), getYvals(times_Lm), 'mo-', getXvals(times_Lm_ideal), getYvals(times_Lm_ideal), 'm:')
                 



p.set_ylabel('Simulation time for 1 sec of net activity (sec)', fontsize=14)

p.set_xlabel('Number of processors', fontsize=14)


lines[0].set_label('Legion')
lines[2].set_label('PadraigPC')
lines[4].set_label('Bernal')
lines[6].set_label('Matthau')
lines[8].set_label('Lemmon')

legend()

fig.set_figheight(8)
fig.set_figwidth(12)

#plt.print_figure()

canvas = FigureCanvas(fig)

canvas.print_eps('Performance.eps')

print dir(fig)


plt.show()


