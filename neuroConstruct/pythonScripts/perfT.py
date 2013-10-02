import matplotlib.pyplot as plt

print "Going to plot performance of simulations on Legion"

times_l = {}

times_l[1] = 6085.21
times_l[2] = 192.17
times_l[4] = 109.67
times_l[8] = 47.47
times_l[16] = 21.26
times_l[24] = 13.14
#times_l[32] = 3.13
#times_l[40] = 3.19
times_l[48] = 6.82
times_l[64] = 5.18



times_s = {}

times_s[1] = 0
times_s[4] = 103.83
times_s[8] = 46.37
times_s[12] = 46.29
times_s[16] = 29.98


times_b = {}

times_b[1] = 187.27
times_b[2] = 85.88
times_b[3] = 40.56
times_b[4] = 29.8


times_e = {}

times_e[1] = 74.9
times_e[2] = 47.63
times_e[3] = 36.61
times_e[4] = 33.93



times_l_t = {}

for i in times_l.keys():
	times_l_t[i] = 64 * times_l[64]/i
	
	
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



lines = plt.loglog(getXvals(times_l_t), getYvals(times_l_t), 'r:', \
								   getXvals(times_l), getYvals(times_l), 'ro-') 
								   
''', \
                   getXvals(times_s), getYvals(times_s), 'go-'

lines = plt.loglog( getXvals(times_l), getYvals(times_l), 'ro-',  \
    getXvals(times_s), getYvals(times_s), 'go-'\
    getXvals(times_b), getYvals(times_b), 'bo-' ) #, getXvals(times_e), getYvals(times_e), 'ko-')
'''
plt.ylabel('Simulation time')
plt.xlabel('Number of processors')

#plt.axis([-3, 36, -10, 200])


print lines[0]

lines[0].set_label('Legion')




plt.show()


