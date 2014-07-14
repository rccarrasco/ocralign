import matplotlib.pyplot as plt

def plot(num, name, xlab, ylab):
    fig = plt.figure(num)
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
    plt.savefig(name + '.png')
    fig.show()


plot(1, 'skew', 'angle (degrees)', 'sharpness')
plot(2, 'lines', 'height', 'darkness')
raw_input()
