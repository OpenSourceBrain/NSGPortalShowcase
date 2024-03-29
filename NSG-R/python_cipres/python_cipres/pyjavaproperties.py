#! /usr/bin/env python

"""
A Python replacement for java.util.Properties class
This is modelled as closely as possible to the Java original.

Created - Anand B Pillai <abpillai@gmail.com>

SEE: https://bitbucket.org/jnoller/pyjavaproperties/src

This is forked from an original at <http://code.activestate.com/recipes/496795/>

LICENSE
It would appear that the authors license the code under the PSF license: <http://docs.python.org/license.html>

Quoth:

'''
License
------------------
As with all ASPN recipes not otherwise licensed prior to July 15, 2008 on
aspn.activestate.com, the original recipe is under PSF License. For more
information, see the ASPN terms of service here:

<http://code.activestate.com/help/terms/>

While the licensing under the PSF license is sub-optimal, it is what it is. See
<http://docs.python.org/license.html> for more information about the PSF
license.
'''

"""


"""
Terri Schwartz: modified this class to fix accessing properties not as dictionary
but as class member.  Also modified to support multiple string values for the
same key.

"""

import sys,os
import re
import time
import traceback
from io import IOBase

PY3 = sys.version_info > (3,)


class IllegalArgumentException(Exception):

    def __init__(self, lineno, msg):
        self.lineno = lineno
        self.msg = msg

    def __str__(self):
        s='Exception at line number %d => %s' % (self.lineno, self.msg)
        return s

class Properties(object):
    """ A Python replacement for java.util.Properties """

    def __init__(self, props=None):

        # Note: We don't take a default properties object
        # as argument yet

        # Dictionary of properties.
        self._props = {}

        # Dictionary of properties with 'pristine' keys
        # This is used for dumping the properties to a file
        # using the 'store' method
        self._origprops = {}
        self._keyorder = []
        # Dictionary mapping keys from property
        # dictionary to pristine dictionary
        self._keymap = {}

        self.othercharre = re.compile(r'(?<!\\)(\s*\=)|(?<!\\)(\s*\:)')
        self.othercharre2 = re.compile(r'(\s*\=)|(\s*\:)')
        self.bspacere = re.compile(r'\\(?!\s$)')

    def __str__(self):
        s='{'
        for key,value in list(self._props.items()):
            print("%s = %s"%(key,value))
            s = ''.join((s,key,'=',str(value),', '))

        s=''.join((s[:-2],'}'))
        return s

    def __parse(self, lines):
        """ Parse a list of lines and create
        an internal property dictionary """

        # Every line in the file must consist of either a comment
        # or a key-value pair. A key-value pair is a line consisting
        # of a key which is a combination of non-white space characters
        # The separator character between key-value pairs is a '=',
        # ':' or a whitespace character not including the newline.
        # If the '=' or ':' characters are found, in the line, even
        # keys containing whitespace chars are allowed.

        # A line with only a key according to the rules above is also
        # fine. In such case, the value is considered as the empty string.
        # In order to include characters '=' or ':' in a key or value,
        # they have to be properly escaped using the backslash character.

        # Some examples of valid key-value pairs:
        #
        # key     value
        # key=value
        # key:value
        # key     value1,value2,value3
        # key     value1,value2,value3 \
        #         value4, value5
        # key
        # This key= this value
        # key = value1 value2 value3

        # Any line that starts with a '#' or '!' is considerered a comment
        # and skipped. Also any trailing or preceding whitespaces
        # are removed from the key/value.

        # This is a line parser. It parses the
        # contents like by line.

        lineno=0
        i = iter(lines)

        for line in i:
            lineno += 1
            line = line.strip()
            # Skip null lines
            if not line: continue
            # Skip lines which are comments
            if line[0] in ('#','!'): continue
            # Some flags
            escaped=False
            # Position of first separation char
            sepidx = -1
            # A flag for performing wspace re check
            flag = 0
            # Check for valid space separation
            # First obtain the max index to which we
            # can search.
            m = self.othercharre.search(line)
            if m:
                first, last = m.span()
                start, end = 0, first
                flag = 1
                wspacere = re.compile(r'(?<![\\\=\:])(\s)')
            else:
                if self.othercharre2.search(line):
                    # Check if either '=' or ':' is present
                    # in the line. If they are then it means
                    # they are preceded by a backslash.

                    # This means, we need to modify the
                    # wspacere a bit, not to look for
                    # : or = characters.
                    wspacere = re.compile(r'(?<![\\])(\s)')
                start, end = 0, len(line)

            m2 = wspacere.search(line, start, end)
            if m2:
                # print 'Space match=>',line
                # Means we need to split by space.
                first, last = m2.span()
                sepidx = first
            elif m:
                # print 'Other match=>',line
                # No matching wspace char found, need
                # to split by either '=' or ':'
                first, last = m.span()
                sepidx = last - 1
                # print line[sepidx]


            # If the last character is a backslash
            # it has to be preceded by a space in which
            # case the next line is read as part of the
            # same property
            while line[-1] == '\\':
                # Read next line
                nextline = next(i)
                nextline = nextline.strip()
                lineno += 1
                # This line will become part of the value
                line = line[:-1] + nextline

            # Now split to key,value according to separation char
            if sepidx != -1:
                key, value = line[:sepidx], line[sepidx+1:]
            else:
                key,value = line,''
            self._keyorder.append(key)
            self.processPair(key, value)

    def processPair(self, key, value):
        """ Process a (key, value) pair """

        oldkey = key
        oldvalue = value

        # Create key intelligently
        keyparts = self.bspacere.split(key)
        # print keyparts

        strippable = False
        lastpart = keyparts[-1]

        if lastpart.find('\\ ') != -1:
            keyparts[-1] = lastpart.replace('\\','')

        # If no backspace is found at the end, but empty
        # space is found, strip it
        elif lastpart and lastpart[-1] == ' ':
            strippable = True

        key = ''.join(keyparts)
        if strippable:
            key = key.strip()
            oldkey = oldkey.strip()

        oldvalue = self.unescape(oldvalue)
        value = self.unescape(value)

        # Patch from N B @ ActiveState
        curlies = re.compile("{.+?}")
        found = curlies.findall(value)

        for f in found:
            srcKey = f[1:-1]
            if srcKey in self._props:
                value = value.replace(f, self._props[srcKey], 1)

        # Terri: changed this.  Value is always a list and 0th value in list is always the last one added.
        self._props.setdefault(key, []).insert(0, value.strip())

        # Check if an entry exists in pristine keys
        if key in self._keymap:
            oldkey = self._keymap.get(key)
            self._origprops[oldkey] = oldvalue.strip()
        else:
            self._origprops[oldkey] = oldvalue.strip()
            # Store entry in keymap
            self._keymap[key] = oldkey

        if key not in self._keyorder:
            self._keyorder.append(key)

    def escape(self, value):

        # Java escapes the '=' and ':' in the value
        # string with backslashes in the store method.
        # So let us do the same.
        newvalue = value.replace(':','\:')
        newvalue = newvalue.replace('=','\=')

        return newvalue

    def unescape(self, value):

        # Reverse of escape
        newvalue = value.replace('\:',':')
        newvalue = newvalue.replace('\=','=')

        return newvalue

    def load(self, stream):
        """ Load properties from an open file stream """

        # For the time being only accept file input streams
        if (PY3 and not isinstance(stream, IOBase)) or (not PY3 and not isinstance(stream, file)):
            raise TypeError('Argument should be a file object!')
        # Check for the opened mode
        if stream.mode != 'r':
            raise ValueError('Stream should be opened in read-only mode!')

        try:
            lines = stream.readlines()
            self.__parse(lines)
        except IOError as e:
            raise

    # Terri: added this method.  Returns None if key isn't in the dictionary
    def getPropertyAsList(self, key):
        return self._props.get(key)

    def getProperty(self, key):
        # Terri: modified
        if self._props.get(key):
            return self._props.get(key)[0]
        return None


    def setProperty(self, key, value):
        """ Set the property for the given key """

        if type(key) is str and type(value) is str:
            self.processPair(key, value)
        else:
            raise TypeError('both key and value should be strings!')

    def propertyNames(self):
        """ Return an iterator over all the keys of the property
        dictionary, i.e the names of the properties """

        return list(self._props.keys())

    def list(self, out=sys.stdout):
        """ Prints a listing of the properties to the
        stream 'out' which defaults to the standard output """

        out.write('-- listing properties --\n')
        for key,value in list(self._props.items()):
            # out.write(''.join((key,'='value,'\n')))
            if not value or not len(value):
                out.write(''.join((key,'=','\n')))
            else:
                for v in value:
                    out.write(''.join((key,'=', v, '\n')))
                

    def store(self, out, header=""):
        """ Write the properties list to the stream 'out' along
        with the optional 'header' """

        if out.mode[0] != 'w':
            raise ValueError('Steam should be opened in write mode!')

        try:
            out.write(''.join(('#',header,'\n')))
            # Write timestamp
            tstamp = time.strftime('%a %b %d %H:%M:%S %Z %Y', time.localtime())
            out.write(''.join(('#',tstamp,'\n')))
            # Write properties from the pristine dictionary
            for prop in self._keyorder:
                if prop in self._origprops:
                    val = self._origprops[prop]
                    out.write(''.join((prop,'=',self.escape(val),'\n')))

            out.close()
        except IOError as e:
            raise

    # Terri: returns the internal dictionary converted such each value is a string, the first one in the list from the raw dict 
    def getPropertyDict(self):
        retval = {}
        for key in self._props:
            retval[key] = self._props[key][0]
        return retval 

    # Terri: returns the internal dictionary object, where each value is a list.
    def getRawPropertyDict(self):
        return self._props

    # Terri: returns the internal dictionary (where each value is a list) as a list of 2-tuples
    # Example: {c1, [v1, v2, v3]}  ->  [(c1, v1), (c1, v2), (c1, v3)]
    def getPropertyDictAsList(self):
        retval = []
        for key in self._props:
            for value in self._props[key]:
                retval.append( (key, value) )
        return retval 

    def __getitem__(self, name):
        """ To support direct dictionary like access """

        return self.getProperty(name)

    def __setitem__(self, name, value):
        """ To support direct dictionary like access """

        self.setProperty(name, value)

    # Terri - I modified this to make it work so that you can access properties as properties.name
    # instead of just properties['name'].
    def __getattr__(self, name):
        """ For attributes not found in self, redirect to the properties dictionary """
        try:
            return self.__dict__[name]
        except KeyError:
            r = self._props.get(name)
            if r:
                return self._props[name][0]
            return None 
                

if __name__=="__main__":
    p = Properties()
    p.load(open('/Users/padraig/pycipres.conf'))
    p.list()
    print(p)
    print(list(p.items()))
    print(p['name3'])
    p['name3'] = 'changed = value'
    print(p['name3'])
    p['new key'] = 'new value'
    p.store(open('test2.properties','w'))
    print(p.name3)
