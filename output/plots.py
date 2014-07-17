import matplotlib.pyplot as plt
import math

def plot(num, name, xlab, ylab):
    fig = plt.figure(num)
    ifile = open(name + '.txt')
    X = []
    Y = []
    for line in ifile:
        tokens = line.split()
        x = float(tokens[0])
        y = float(tokens[1])
        X.append(x)
        Y.append(y)

    plt.xlabel(xlab)
    plt.ylabel(ylab)
    plt.plot(X, Y)
    plt.savefig(name + '.png')
    fig.show()


plot(1, 'skew', 'angle (degrees)', 'sharpness')
plot(2, 'lines', 'height', 'darkness')
#plot(3, 'histo', 'lumin', 'num')

raw_input()
