import matplotlib.pyplot as plt

def plot(name, source, xlab, ylab):
    ifile = open(name + '.txt')
    X = []
    Y = []
    for line in ifile:
        tokens = line.split()
        X.append(tokens[0])
        Y.append(tokens[1])

    plt.xlabel(xlab)
    plt.ylabel(ylab)
    plt.plot(X, Y)
    plt.savefig('skew.png')
    plt.show()


plot('skew', 'angle (degrees)', 'sharpness')
plot('lines', 'height', 'darkness')
