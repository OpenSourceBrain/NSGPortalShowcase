import matplotlib.pyplot as plt

print "Going to plot performance of simulations on Legion"

times_l = {}

times_l[1] = 118.76
times_l[2] = 41.56
times_l[4] = 19.19
times_l[8] = 9.57
times_l[16] = 5.23
times_l[24] = 4.08
times_l[32] = 3.13
times_l[40] = 3.19
times_l[48] = 2.72
times_l[64] = 2.16



times_s = {}

times_s[1] = 84.33
times_s[4] = 16.89
times_s[8] = 8.19
times_s[12] = 5.35
times_s[16] = 4.02


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


times_s_t = {}

for i in times_s.keys():
	times_s_t[i] = times_s[1]/i

times_l_t = {}

for i in times_l.keys():
	times_l_t[i] = times_l[1]/i
	
times_b_t = {}

for i in times_b.keys():
	times_b_t[i] = times_b[1]/i
	
times_e_t = {}

for i in times_e.keys():
	times_e_t[i] = times_e[1]/i


lines = plt.loglog(getXvals(times_s_t), getYvals(times_s_t), 'g:', \
    getXvals(times_s), getYvals(times_s), 'go-' )

plt.ylabel('Simulation time')
plt.xlabel('Number of processors')

#plt.axis([-3, 36, -10, 200])


print lines[0]

lines[0].set_label('Legion')

c = plt
print c.__class__
print c
print dir(c)


print plt.xticks()

plt.show()


