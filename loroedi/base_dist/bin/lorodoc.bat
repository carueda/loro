@echo off
java "-DISO3_LANG=$ISO3_LANG" "-DLOROEDIDIR=$INSTALL_PATH" -cp "$INSTALL_PATH\lib\loro.jar" loro.tools.LoroDocumentador %1 %2 %3 %4 %5 %6 %7 %8 %9
