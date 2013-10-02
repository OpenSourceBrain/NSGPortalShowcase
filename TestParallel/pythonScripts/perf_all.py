import matplotlib.pyplot as plt
from pylab import *

print "Going to plot performance of simulations on Legion"

T_time = 20

times_l_T = {}

times_l_T[1] = 6085.21
times_l_T[2] = 192.17
times_l_T[4] = 109.67
times_l_T[8] = 47.47
times_l_T[16] = 21.26
times_l_T[24] = 13.14
#times_l[32] = 3.13
#times_l[40] = 3.19
times_l_T[48] = 6.82
times_l_T[64] = 5.18

for i in times_l_T.keys():
	times_l_T[i] = 1000 * times_l_T[i] / T_time


G_time = 30

times_l_G = {}

times_l_G[1]=239.5
times_l_G[4]=47.8
times_l_G[8]=21.0
times_l_G[16]=8.8
times_l_G[24]=6.0
times_l_G[32]=4.7
times_l_G[40]=3.9
times_l_G[48]=3.6

for i in times_l_G.keys():
	times_l_G[i] = 1000 * times_l_G[i] / G_time
	


A_time = 20000

times_l_A = {}

times_l_A[1] = 1597.62
times_l_A[2] = 816.27
times_l_A[4] = 369.7
times_l_A[8] = 189.15
times_l_A[16] = 103.45
times_l_A[24] = 80.43
times_l_A[32] = 62.21
#times_l_A[40] = 3.19
times_l_A[48] = 53.88
times_l_A[64] = 44.21

for i in times_l_A.keys():
	times_l_A[i] = 1000 * times_l_A[i] / A_time




times_l_t_T = {}

for i in times_l_T.keys():
	proc_norm = min(times_l_T.keys())
	times_l_t_T[i] = proc_norm * times_l_T[proc_norm]/i


times_l_t_A = {}

for i in times_l_A.keys():
	proc_norm = min(times_l_A.keys())
	times_l_t_A[i] = proc_norm * times_l_A[proc_norm]/i

times_l_t_G = {}

for i in times_l_G.keys():
	proc_norm = min(times_l_G.keys())
	times_l_t_G[i] = proc_norm * times_l_G[proc_norm]/i
	
	
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

lines = p.loglog(getXvals(times_l_t_T), getYvals(times_l_t_T), 'r:', \
								   getXvals(times_l_T), getYvals(times_l_T), 'ro-', \
								   getXvals(times_l_t_G), getYvals(times_l_t_G), 'b:', \
								   getXvals(times_l_G), getYvals(times_l_G), 'bo-', \
								   getXvals(times_l_t_A), getYvals(times_l_t_A), 'g:', \
								   getXvals(times_l_A), getYvals(times_l_A), 'go-') 



p.set_ylabel('Simulation time for 1 sec of network activity (sec)', fontsize=14)

p.set_xlabel('Number of processors', fontsize=14)


lines[1].set_label('Neocortex')
lines[3].set_label('Cerebellum')
lines[5].set_label('Abstract network')

legend()

fig.set_figheight(8)
fig.set_figwidth(12)

#plt.print_figure()

canvas = FigureCanvas(fig)

canvas.print_eps('Performance.eps')

print dir(fig)


plt.show()


