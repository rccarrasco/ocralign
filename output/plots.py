import matplotlib.pyplot as plt
ifile = open('skew.txt')
X = []
Y = []
for line in ifile:
    tokens = line.split()
    X.append(tokens[0])
    Y.append(tokens[1])

plt.xlabel('angle (degrees)')
plt.ylabel('sharpness')
plt.plot(X, Y)
plt.savefig('skew.png')
plt.show()
    
