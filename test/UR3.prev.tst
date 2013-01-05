This is ftp.gnu.org, the FTP server of the the GNU project.

Comments, suggestions, problems and complaints should be reported via
email to <gnu@gnu.org>.

gnu/		Contains GNU programs and documents that we develop for the GNU
		system (or pointers on where to get the programs, if we don't
		keep the files here).  These are programs that fit the
		definition of GNU software at:
		http://www.gnu.org/philosophy/categories.html#GNUsoftware

old-gnu/	Older versions of GNU software.

non-gnu/	We distribute some non-GNU programs through our FTP server, or
		provide pointers to where they are.  We put these
		programs/pointers in this directory since they are not
		developed by the GNU project. They are, of course, part of
		the GNU system. See:
		http://www.gnu.org/philosophy/categories.html#TheGNUsystem

third-party	Contains GNU software that has been modified by third
		parties.  We don't necessarily know the specifics of
		what these modifications do or how these modified versions
		work.  We provide this directory as a service to GNU users
		who might find these modifications useful.

iso		Contains bootable CD images (ISO9660) of a development
		snapshot of the Debian GNU/Hurd complete operating system.

ls-lrR.txt.gz	The output of `ls -lrR' run from this directory. This can
		be used to see what files are here. This is a gzip'ed version of the file.

lpf.README	A description of where to find information on the League for
		Programming Freedom, since this information is not kept here 
		anymore.

There are also .sig files, which contain detached GPG signatures of the above
files, automatically signed by the same script that generates them.

You can verify the signatures for gnu project files with the keyring file from:
  ftp://ftp.gnu.org/gnu/gnu-keyring.gpg

In a directory with the keyring file, the source file to verify and the
signature file, the command to use is:

  $ gpg --verify --keyring ./gnu-keyring.gpg foo.tar.xz.sig 


