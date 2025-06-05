import queue
from pprint import pprint
from collections import Counter, defaultdict
import operator
import random
from gettysburgaddress import GettysburgAddress
from taoteching import TaoTeChing
from hamlet import Hamlet
from fromfile import FromFile
import textwrap
import re


class Ns:

    def __init__(self, n, source):
        # largest number of ngrams to chop
        self._n = n

        # clean up the source
        self._source = (source
                        .replace('***', ' ')
                        .replace('...', ' , ')
                        .replace('--', ' , ')
                        .replace('.', ' .')
                        .replace('?', ' ?')
                        .replace('!', ' !')
                        .replace(',', ' ,')
                        .replace(' "', ' [ ')
                        .replace('""', ' [ ')
                        .replace('" ', ' ] ')
                        .replace('"', ' [ ')
                        .replace('  ', ' ')
                        .replace('(', ' , ')
                        .replace(')', ' , ')
                        .replace(';', ' , ')
                        .replace('\n', '')
                        .split(' '))

        # a queue
        self._q = queue.Queue(0)

        # ngrams we've already considered
        self._qs = set()

        # overall count of transitions
        self.ts = Counter()

        # count of ngrams in pre
        self.pres = Counter()

        # count of ngrams in post
        self.posts = Counter()

        # the scored ngrams
        self._scored = None

    def _transition(self, i, a, b):
        """Build a tuple of strings from index i with lengths a and b"""
        return tuple(self._source[i:i + a]), tuple(self._source[i + a:i + a + b])

    def _consider(self, i, a, b):
        """If these indexes and lengths are good, enqueue and tally them up"""
        if (i + a < len(self._source) and i + a + b < len(self._source)
                and a <= self._n and b <= self._n
                and not (i, a, b) in self._qs):
            # print('*', i, a, b)
            self._qs.add((i, a, b))
            self._q.put((i, a, b))

            # count the actual tuple
            t = self._transition(i, a, b)
            self.ts.update([t])
            self.pres.update([t[0]])
            self.posts.update([t[1]])

    def go(self):
        """Traverse the text to produce all the transition tuples"""
        self._q.put((0, 1, 1))
        while not self._q.empty():
            i, a, b = self._q.get()
            # print(i, a, b)
            self._consider(i, a + 1, b)
            self._consider(i, a, b + 1)
            self._consider(i + 1, 1, 1)
        return self

    def score(self, t):
        """Generate the score for a transition tuple"""
        ((pre, post), over) = t
        first = self.pres[pre]
        last = self.posts[post]
        s = (1.0 + over) / (10.0 + first + last)
        # print(pre, post, over, first, last, s)
        return s

    def scored(self):
        """Returns a dict of transitions with the associated score"""
        d = defaultdict(dict)
        total = 0.0
        for t in self.ts.items():
            s = self.score(t)
            d[t[0][0]][t[0][1]] = s
            total += s
        self._scored = d
        return d, total

    def choose(self, current):
        print('->', (current[-1:],), tuple(current[-2:]), tuple(current[-3:]))
        a = {**self._scored[tuple(current[-1:])],
             **self._scored[tuple(current[-2:])],
             **self._scored[tuple(current[-3:])]}
        # pprint(a)
        pick = sum(a.values()) * random.random()
        for aa in a.items():
            pick -= aa[1]
            if pick <= 0:
                print('...', aa[0])
                return aa[0]
        print('... Nope')
        return []


if __name__ == '__main__':
    # ns = Ns(5, FromFile('incidentInTheLectureHall.txt').txt).go()
    getty = GettysburgAddress().txt
    # gate = FromFile('gatelessgate.txt').txt
    # shakes = FromFile('shakespeare100-0.txt').txt
    ham = Hamlet().txt
    # tao = TaoTeChing().txt
    # txt = FromFile('myth.txt').txt
    gilgamesh = FromFile('eog_djvu.txt').txt
    partytime = FromFile('partytimeX.txt').txt
    ns = Ns(3, gilgamesh).go()
    # ns = Ns(5, TaoTeChing().txt).go()
    ns.scored()

    # print("-- Scored")
    # pprint(ns.scored())

    out = ['.']

    for _ in range(160):
        out += ns.choose(out)

    out = (' '.join(out)
           .replace(' .', '. ')
           .replace(' ?', '? ')
           .replace(' !', '! ')
           .replace(' ,', ', ')
           .replace('( ', '( ')
           .replace(' )', ') ')
           .replace('\n', '')
           .replace('^', '\n')
           .replace('[', ' " ')
           .replace(']', '')
           .replace(' ;', ';'))
    out = re.sub(r'  +', ' ', out)

    print('\n'.join(textwrap.wrap(out, replace_whitespace=False)))
