from setuptools import setup

setup(name='python_cipres',

    version='0.9',

    description='CIPRES REST API Client',

    long_description = open('README.txt').read() + open('CHANGES.txt').read(),

    url='http://www.phylo.org/restusers',

    author='Terri Schwartz',

    author_email='terri@sdsc.edu',

    license='MIT',

    classifiers = [
        'Development Status :: 5 - Production/Stable',
        'Intended Audience :: Science/Research',
        'Programming Language :: Python :: 2',
        'Programming Language :: Python :: 3',
        'Programming Language :: Python',

    ],

    keywords='cipres cra phylogenetics xsede',


    install_requires=[
        "pymysql >= 0.5",
        "requests >= 2.5.3",
        "pystache >= 0.5.3",
    ],

    scripts=[
        "bin/tooltest.py",
        "bin/cipresjob.py",
    ],

    packages=['python_cipres'],

    # It seems that with "setup.py sdist", MANIFEST.in controls which extra files are
    # put into the sdist archive.  However those extra files won't be installed to user's
    # system unless we have include_package_data=True here.
    include_package_data=True,

    zip_safe=False)
