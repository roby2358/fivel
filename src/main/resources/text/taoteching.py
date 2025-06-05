import re


class TaoTeChing:

    def __init__(self):
        with open('taoteching.txt') as f:
            self.txt = ''.join(f.readlines())
            self.txt = re.sub(r'Chapter [0-9]+', '', self.txt)
            self.txt = re.sub(r'\n[0-9]+\. ', '', self.txt)
            self.txt = self.txt\
                .replace('(', '') \
                .replace(')', '') \
                .replace(';', ',') \
                .replace('  ', ' ') \
                .replace('\n\n', '') \
                .replace('\n', ' ')


if __name__ == '__main__':
    print(TaoTeChing().txt)
