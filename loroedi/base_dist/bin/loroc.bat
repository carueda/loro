@echo off
java "-DLOROEDIDIR=$INSTALL_PATH" -cp "$INSTALL_PATH\lib\loro.jar" loro.tools.LoroCompilador %1 %2 %3 %4 %5 %6 %7 %8 %9
