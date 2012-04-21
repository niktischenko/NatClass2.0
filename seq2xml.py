#!/usr/bin/python
import sys
import codecs
import string

def dispatchLine(line, i) :
	column = line.split(':')
	print '    <Entity name="' + column[0] + '_v' + unicode(i) + '">'
	print '        <Attributes>'

	for c in range(len(column[1])) :
		ind = chr(ord('A') + c)
		print '            <Attribute name="' + str(ind) + '" value="' + column[1][c] + '"/>'
	
	print '        </Attributes>'
	print '    </Entity>'


def main(argv):
	fileObj = codecs.open( sys.argv[1], "r", "utf-8" )
	print '<?xml version="1.0" encoding="UTF-8" standalone="no"?>'
	print '<Entities>'
	
	i = 0
	for line in fileObj :
		i = i + 1
		dispatchLine(line.strip(' \t\n\r'), i)
    
	print "</Entities>"
	return 0

if __name__ == "__main__" :
	main(sys.argv[1:])