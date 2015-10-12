package jadd;

import java.io.IOException;

import org.bridj.DynamicFunction;
import org.bridj.NativeLibrary;
import org.bridj.Pointer;

class CUtils {
    private static NativeLibrary libc;
    private static DynamicFunction<Pointer<?>> fopenHandle;
    private static DynamicFunction<?> fcloseHandle;

    public static String ACCESS_WRITE = "w";

    static {
        try {
            // TODO Make libc's path portable!
            libc = NativeLibrary.load("/lib/x86_64-linux-gnu/libc.so.6");

            Pointer<?> fopenAddress = libc.getSymbolPointer("fopen");
            fopenHandle = fopenAddress.asDynamicFunction(null, Pointer.class, Pointer.class, Pointer.class);

            Pointer<?> fcloseAddress = libc.getSymbolPointer("fclose");
            fcloseHandle = fcloseAddress.asDynamicFunction(null, int.class, Pointer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pointer<?> fopen(String fileName, String access) {
        return fopenHandle.apply(Pointer.pointerToCString(fileName),
                                 Pointer.pointerToCString(access));
    }

    public static int fclose(Pointer<?> openFile) {
        return (int) fcloseHandle.apply(openFile);
    }
}
