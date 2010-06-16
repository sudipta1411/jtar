package org.xeustechnologies.jtar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JTarTest {
    static final int BUFFER = 2048;

    /**
     * Tar the given folder
     * 
     * @throws IOException
     */
    @Test
    public void tar() throws IOException {
        FileOutputStream dest = new FileOutputStream( "c:/test/test.tar" );
        TarOutputStream out = new TarOutputStream( new BufferedOutputStream( dest ) );

        tarFolder( null, "c:/test/tartest", out );

        out.close();
    }

    /**
     * Untar the tar file
     * 
     * @throws IOException
     */
    @Test
    public void untar() throws IOException {
        File zf = new File( "c:/test/test.tar" );
        String destFolder = "c:/test/untartest";

        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream( zf );
        TarInputStream tis = new TarInputStream( new BufferedInputStream( fis ) );
        TarEntry entry;
        while(( entry = tis.getNextEntry() ) != null) {
            System.out.println( "Extracting: " + entry.getName() );
            int count;
            byte data[] = new byte[BUFFER];

            if( entry.isDirectory() ) {
                new File( destFolder + "/" + entry.getName() ).mkdirs();
                continue;
            } else {
                new File( destFolder + "/" + entry.getName().substring( 0, entry.getName().lastIndexOf( '/' ) ) )
                        .mkdirs();
            }

            FileOutputStream fos = new FileOutputStream( destFolder + "/" + entry.getName() );
            dest = new BufferedOutputStream( fos );

            while(( count = tis.read( data ) ) != -1) {
                dest.write( data, 0, count );
            }

            dest.flush();
            dest.close();
        }
        tis.close();
    }

    public void tarFolder(String parent, String path, TarOutputStream out) {
        try {
            BufferedInputStream origin = null;
            File f = new File( path );
            String files[] = f.list();

            // is file
            if( files == null ) {
                files = new String[1];
                files[0] = f.getName();
            }

            parent = ( ( parent == null ) ? ( f.isFile() ) ? "" : f.getName() + "/" : parent + f.getName() + "/" );

            for( int i = 0; i < files.length; i++ ) {
                System.out.println( "Adding: " + files[i] );
                File fe = f;
                byte data[] = new byte[BUFFER];

                if( f.isDirectory() ) {
                    fe = new File( f, files[i] );
                }

                if( fe.isDirectory() ) {
                    String[] fl = fe.list();
                    if( fl != null && fl.length != 0 ) {
                        tarFolder( parent, fe.getPath(), out );
                    } else {
                        TarEntry entry = new TarEntry( fe, parent + files[i] + "/" );
                        out.putNextEntry( entry );
                    }
                    continue;
                }

                FileInputStream fi = new FileInputStream( fe );
                origin = new BufferedInputStream( fi );

                TarEntry entry = new TarEntry( fe, parent + files[i] );
                out.putNextEntry( entry );

                int count;
                int bc = 0;
                while(( count = origin.read( data ) ) != -1) {
                    out.write( data, 0, count );
                    bc += count;
                }

                out.flush();

                origin.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
