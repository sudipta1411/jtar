package org.xeustechnologies.jtar;

import java.io.File;

public class TarUtils {
    public static long calculateTarSize(File path) {
        return tarSize( path ) + TarConstants.EOF_BLOCK;
    }

    private static long tarSize(File dir) {
        long size = 0;
        if( dir.isFile() ) {
            size += entrySize( dir.length() );
        } else {
            File[] subFiles = dir.listFiles();

            if( subFiles != null && subFiles.length > 0 ) {
                for( File file : subFiles ) {
                    if( file.isFile() ) {
                        size += entrySize( file.length() );
                    } else {
                        size += calculateTarSize( file );
                    }
                }
            } else {
                // Empty folder header
                size += TarConstants.HEADER_BLOCK;
            }
        }

        return size;
    }

    private static long entrySize(long fileSize) {
        long size = 0;
        size += TarConstants.HEADER_BLOCK; // Header
        size += fileSize; // File size
        size += TarConstants.DATA_BLOCK - ( fileSize % TarConstants.DATA_BLOCK ); // pad

        return size;
    }
}
