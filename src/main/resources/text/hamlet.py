import re

#/enterbasefrompasscode COSMICTRANSPORT-6608
#/enterbasefrompasscode WOOPA1111-7022

class Hamlet:

    def __init__(self):
        with open('hamlet.txt') as f:
            self.txt = ''.join(f.readlines())
            self.txt = re.sub(r'\n([A-Z]+\.)', r' ^\1 ', self.txt)
            self.txt = self.txt \
                .replace('(', '') \
                .replace(')', '') \
                .replace('[_', ' ( ') \
                .replace('_]', ' ) ') \
                .replace(';', ',') \
                .replace('—', ' ') \
                .replace('--', ' ') \
                .replace('â€”', ' ') \
                .replace('  ', ' ') \
                .replace('\n\n', '') \
                .replace('\n', ' ')


if __name__ == '__main__':
    print(Hamlet().txt)
