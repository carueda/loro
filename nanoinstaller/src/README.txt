nanoInstaller - An installer utility in Java
(c) Carlos Rueda - carueda@users.sf.net

GOAL
    The main goal of the nanoInstaller project is to offer an ultra-light
    tool to make installers for Java programs.

    The current version of the nanoInstaller is for you if:

        - you want to say the least possible to make your installer;

        - you want a simple GUI interface that presents your
          application and lets the user change a proposed destination
          directory;

        - you only want to tranfer all the files under a specific directory
          (and all of its subdirectories) to the destination in the user machine;

        - you need to customize some of the transferred files (typically
          scripts, documents), such that certain "tags" are replaced with
          values only known at installation time (like "installation directory",
          and system properties).

        - You want the installer to write a ${user.home}/myapp.properties file,
          so your application will be able to know where it was installed.

    The current version of the nanoInstaller is NOT for you if:
        - you expect capabilities not mentioned above (like extensible architecture,
          shortcuts, organization in packs, uninstallers, more languages, etc.).
          In this case, I strongly recommend IzPack (http://www.izforge.com/izpack/),
          a much more supported (and serious ;^) project.

INSTALL
    Assuming nanoinstaller.jar is the installer program to install the
    nanoInstaller ;^), just type:
        java -jar nanoinstaller.jar
    and follow the instructions.

BUILD
    To build the project, use the Jakarta Ant tool (http://jakarta.apache.org/ant):

        * Go to subdirectory src/ under the installation;

        * Read (and edit if necessary) the build.properties file;

        * Type:
                ant
          to generate the nanoInstaller compiler nic.jar .
          (This executable archive contains the complete tool for the end user.)

        * If you want to re-generate the nanoInstaller installer itself:
                ant auto-installer

    See build.properties and build.xml for more details.


USER MANUAL  (March 5, 2002)
    The nanoInstaller compiler, nic.jar, is all what you, as an end user, need.

    To make an installer program, the compiler expects the following information:

        - A base directory that contains all what you want to be installed.

        - A properties file containing:
            - Presentation info (application name, image, etc).
            - A "readme" file (in HTML format if desired) to show after finishing
              an installation.
            - A list of files to be installed ONLY on a specific O.S.
            - A list of files where replacement of special tags should be done.

        - The desired name for the executable installer jar file.

    Give the compiler the above information and, voila, you have your installer!
    The basic compiler usage is as follows:

        java -jar nic.jar install-properties baseDirectory installerName

    For more details, call the program above with no arguments (for example, you
    can change the natural language used for messages during installation).
    Also, read the sample properties file included (sample.properties).
    You can make your first nano-installer almost inmediately!  Enjoy.

LIMITATIONS
    - Included language definitions don't work under Java 1.2.2. (It's a
      Java bug, not nanoInstaller's.) **Workaround: give the compiler an
      explicit definition with the -lang option.

    - There is no way to "chmod +x" to make scrips executable under Linux/Unix
      systems.

    - I have tested the tool using:
         Java 1.3 and 1.4 on Win2000 - OK
         Java 1.3.1 on GNU/Linux     - OK
         Java 1.2.2 on GNU/Linux     - See above

TO-DO
    - Let the end user choose the natural language for installation.
    - Show application license information before installing.
    - A non-GUI installation.
    - "chmod +x" to make scrips executable under Linux/Unix systems.

    NOTE: Keep this as a "nano" project; don't be tempted to include so much
    features. Otherwise, join the IzPack team of contributors; in fact, I am
    one of those ;^) but I still continue to maintain the nanoInstaller 'cause
    it meets what I need for some of my projects (and also for fun).

LICENSE
    nanoInstaller is covered by the GNU General Public License.
    You should have received a copy of the GNU General Public License
    along with this program (COPYING.txt); if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

HISTORY (most recent first)

3/05/02
    - New _nanoInstaller_/resource/info.properties to keep the version of
      the system. This resource is read by the compiler and from the build.xml
      to make the auto-installer.

3/04/02
    - Added a simple mechanism to show messages in a specific language during
      installation. This is established at compile time (that is, when running
      the nic compiler) and cannot be changed during installation (yet).
    - Much better (still short) documentation.

2/27/02
    - A much better (but still simple as required) version. Now, there is an
      'installer,' and also a 'compiler' to make an installer archive. See
      the user manual above.
    - Documentation improved.
    - The complete system (nic.jar) is still < 20Kb !

9/12/01
    Previous configuration properties are kept (if any).

8/17/01
    Jakarta Ant used to build the tool.

7/11/2001
    Final revision of a first version.

7/06/2001
    Links attended in html documents.

7/02/2001
    A "readme post-instalacion" file gets displayed after finishing an installation.

6/22/2001
    Started this nano-project (only for my own use).

