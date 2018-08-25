@echo off
REM Setze die Codepage auf "West European Latin", die voll unterstuetzt wird und Definition fuer Umlaute, die von Echo bei "UFT-8" nicht unterstuetzt werden
Chcp 1252
Set "varue=ü"
Set "varss=ß"
Set "varae=ä"
Set "varoe=ö"
REM Setze Codepage auf "UFT-8", die nur von manchen Befehlen unterstuetzt wird
REM Wichtig: Echo von UFT-8 Zeichen innerhalb der Konsole fuehrt zu Crash des Skriptes, deshalb kein direktes Echo von Variablen auf die Konsolenoberflaeche nur in Text- oder INI-Dateien
Chcp 65001
REM Erlaube globale Variablendefinition in For-Schleifen, dafuer allerdings keine Verwendung von Ausrufezeichen mehr in Variablen moeglich
Setlocal enabledelayedexpansion
REM Ueberpruefung, ob Log neu generiert oder fortgesetzt werden soll
If Not Exist "ResumeLog.txt" (
  Echo Kellermusik Log>KellermusikLog.txt
) Else (
  Echo Log wird nach Neustart des Skriptes fortgesetzt>>KellermusikLog.txt
  Del "ResumeLog.txt"
)
If Not Exist "KellermusikConfig.ini" (
  Echo Kellermusik Konfigurationsdatei>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo DropboxPath=Standard>>KellermusikConfig.ini
  Echo ; Pfad des lokalen Dropbox-Ordners>>KellermusikConfig.ini
  Echo ; Der Standardwert "Standard" bedeutet, der standardm%varae%%varss%ig definierte Dropbox-Ordner wird verwendet>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo DropboxMusicFolderName=Kellermusik>>KellermusikConfig.ini
  Echo ; Name des zu benutzenden Dropbox Musikordners>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo TargetPath=local>>KellermusikConfig.ini
  Echo ; Pfad des Zielverzeichnisses>>KellermusikConfig.ini
  Echo ; Der Standardwert"local" bedeutet, als Zielverzeichnis wird das momentane Verzeichnis der Batch-Datei gew%varae%hlt>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo PlaylistName=Kellermusik>>KellermusikConfig.ini
  Echo ; Name der zu erstellenden Wiedergabeliste>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo EnableFolderExclusion=true>>KellermusikConfig.ini
  Echo ; "false" kein Ausschlie%varss%en von Ordnern im Zielverzeichnis f%varue%r die Erstellung der Wiedergabeliste>>KellermusikConfig.ini
  Echo ; "true" Ausschlie%varss%en von Ordnern im Zielverzeichniss m%varoe%glich>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo EnablePlaylistExclusionRenaming=true>>KellermusikConfig.ini
  Echo ; "false" keine automatische Neubenennung der Wiedergabeliste bei aktiviertem Ordnerausschluss>>KellermusikConfig.ini
  Echo ; "true" automatische Neubenennung der Wiedergabeliste bei aktiviertem Ordnerausschluss>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo TracksPP=20>>KellermusikConfig.ini
  Echo ; Maximale Anzahl zur Wiedergabeliste hinzugef%varue%gter Tracks pro Person>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo RandomSelectionCycles=999>>KellermusikConfig.ini
  Echo ; Maximale Anzahl der Wiederholungen f%varue%r das zuf%varae%lliges Ziehen eines Tracks,>>KellermusikConfig.ini
  Echo ; um mehrfaches Vorkommen auf der selben Wiedergabeliste zu verhindern>>KellermusikConfig.ini
  Echo ; Der Standardwert betr%varae%gt 999 und stellt einen guten Mittelwert aus Skriptperformance und Sicherheit dar>>KellermusikConfig.ini
  Echo ; Gr%varoe%%varss%ere Werte erh%varoe%hen die Wahrscheinlichkeit der erfolgreichen Wiedergabelistenerstellung,>>KellermusikConfig.ini
  Echo ; dies kann bei einer sehr hohen "TracksPP"-Varibale wichtig sein, verlangsamt allerdings die Ausf%varue%hrung des Skriptes>>KellermusikConfig.ini
  Echo ; Niedrigere Werte steigern die Performance und erh%varoe%hen die Wahrscheinlichkeit des Eastereggs>>KellermusikConfig.ini
  Echo ; Vorsicht: Sehr niedrige Werte k%varoe%nnen zu nicht vorgesehenem Verhalten f%varue%hren,>>KellermusikConfig.ini
  Echo ; Vorsicht: wie beispielsweise der wiederholten, unkontrollierten Ausf%varue%hrung des Skriptes>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo EnableEasterEgg=true>>KellermusikConfig.ini
  Echo ; "false" einfacher Neustart des Skripts, falls die Erstellung einer Wiedergabeliste scheitert>>KellermusikConfig.ini
  Echo ; "true" spielt vor dem Neustart ein Easteregg ab, falls die Erstellung einer Wiedergabeliste scheitert>>KellermusikConfig.ini
  Echo: >>KellermusikConfig.ini
  Echo Keine Konfigurationsdatei gefunden>>KellermusikLog.txt
  Echo KellermusikConfig wird erstellt>>KellermusikLog.txt
  Echo Bitte passen sie die Konfigurationsdatei nach ihren W%varue%nschen an und starten sie das Skript erneut>>KellermusikLog.txt 
  Echo Keine Konfigurationsdatei gefunden
  Echo KellermusikConfig wird erstellt
  Echo Bitte passen sie die Konfigurationsdatei nach ihren Wuenschen an und starten sie das Skript erneut
  Pause 
) Else (
  Echo Konfigurationsdatei wird ausgelesen
  For /F "delims== skip=1 eol=; tokens=1*" %%a in (KellermusikConfig.ini) do (
    If %%a==DropboxPath (
      Echo %%a = %%b>>KellermusikLog.txt
      If %%b==Standard (
        Set "dbpath=%UserProfile%\Dropbox\"
      ) Else (
        Set "dbpath=%%b"
      )
      Echo Lokaler Dropbox-Pfad = !dbpath!>>KellermusikLog.txt
    )
    If %%a==DropboxMusicFolderName (
      Echo %%a = %%b>>KellermusikLog.txt
      Set "dbpath=!dbpath!%%b\"
      Echo Lokaler Dropbox-Musik-Pfad = !dbpath!>>KellermusikLog.txt
    )
    If %%a==TargetPath (
      Echo %%a = %%b>>KellermusikLog.txt
      If %%b==local (
        Set "tpath=%~dp0%"
      ) Else (
        Set "tpath=%%b"
      )
      Echo Zielpfad = !tpath!>>KellermusikLog.txt
    )
    If %%a==PlaylistName (
      Echo %%a = %%b>>KellermusikLog.txt
      Set  "plname=%%b"
    )
    If %%a==EnableFolderExclusion (
      Echo %%a = %%b>>KellermusikLog.txt
      If %%b==true (
        If Not Exist "!tpath!Exclude.ini" (
          Echo Ausschluss-Konfigurationsdatei>!tpath!Exclude.ini
          Echo ; Hier aufgef!varue!hrte Ordner werden nicht f!varue!r die Erstellung der Wiedergabeliste berücksichtigt>>!tpath!Exclude.ini
          Echo ; Jeder Ordnername muss in einer neuer Zeile aufgef%varue%hrt werden>>!tpath!Exclude.ini
        )
      )
      Set  "eexcl=%%b"
    )
    If %%a==EnablePlaylistExclusionRenaming (
      Echo %%a = %%b>>KellermusikLog.txt
      Set "renexcl=%%b"
    )
    If %%a==TracksPP (
      Echo %%a = %%b>>KellermusikLog.txt
      Set /A "trackspp=%%b"
    )
    If %%a==RandomSelectionCycles (
      Echo %%a = %%b>>KellermusikLog.txt
      Set /A "rsi=%%b"
    )
    If %%a==EnableEasterEgg (
      Echo %%a = %%b>>KellermusikLog.txt
      Set "easter=%%b"
    )
  )
  REM Setze den Arbeitspfad zum Ausgangspfad fuer erleichtertes Arbeiten, Popd moeglich 
  Pushd "!dbpath!"
  Echo Verschiebe und ersetze neue Dateien, aktualisiere bestehende Dateien
  REM Checke alle Ordner im Ausgangsverzeichnis
  For /D %%n in ("*") do (
    If %%n neq Toolbox (
      REM Checke alle Daten im korrespondierenden Ordner
      For /F "tokens=*" %%v in ('dir /b %%n') do (
        REM Verschiebe Datei falls nicht im Zielverzeichnis existent
        If Not Exist "!tpath!%%n\%%v" (
          Set /A "svd=0"
          REM Ueberpruefe auf Stellvertreterdatei
          For /F "usebackq" %%w in ("%%n/%%v") do (
            If %%w equ Stellvertreterdatei (
              Set /A "svd=1"
              Echo Fehler: Versuche Stellvertreterdatei zu verschieben, Vorgang abgebrochen
              Echo Fehler: Versuche Stellvertreterdatei zu verschieben, Vorgang abgebrochen>>%~dp0%KellermusikLog.txt
            )
          )
          REM Verschiebe Datei und ersetze diese mit Stellvertreterdatei
          If !svd!==0 (
            Echo Verschiebe und ersetze %%n/%%v>>%~dp0%KellermusikLog.txt 
            XCopy "!dbpath!%%n\%%v" "!tpath!%%n\" /I
            Echo Stellvertreterdatei>%%n/%%v
          )       
        ) Else (
          REM Falls Datei im Zielverzeichnis vorhanden, checke auf moegliches Update durch Uberpruefung, ob Stellvertreterdatei
          For %%q in ("%%n/%%v") do (
            Set /A "svd=0"
            For /F "usebackq" %%w in ("%%n/%%v") do (
              If %%w equ Stellvertreterdatei (
                Set /A "svd=1"
              )
            )
            If !svd!==0 (
              Echo Update %%n/%%v>>%~dp0%KellermusikLog.txt
              XCopy "!dbpath!%%n\%%v" "!tpath!%%n\" /I /Y
              Echo Stellvertreterdatei>%%n/%%v
            )
          )
        ) 
      )
    ) Else (
      REM Vergleich Stellvertreterdateien des Anfrageverzeichnisses mit allen Dateien des Zielverzeichnisses, falls vorhanden, ersetze Stellvertreterdatei mit Orginaldatei
      Echo Verarbeite Trackanfragen
      For /F "tokens=*" %%v in ('dir /b "Toolbox\RequestBox"') do (
        For /F "usebackq" %%w in ("Toolbox\RequestBox\%%v") do (
          If %%w equ Stellvertreterdatei (
            For /D %%l in ("*") do (
              If Exist "!tpath!%%l/%%v" (
                Echo Request f!varue!r %%v verarbeitet>>%~dp0%KellermusikLog.txt 
                XCopy "!tpath!%%l\%%v" "!dbpath!Toolbox\RequestBox\%%v" /I /Y      
              )
            )
          ) 
        )  
      )
    )
  )
  REM Setze den Arbeitspfad zum Zielpfad fuer erleichtertes Arbeiten, Popd moeglich
  Pushd "!tpath!"
  Echo Entferne veraltete Dateien
  REM Enterne Dateien aus dem Zielverzeichnis, die nicht mehr im Ausgangsverzeichnis vorhanden sind
  For /D %%m in ("*") do (
    For /F "tokens=*" %%c in ('dir /b %%m') do (
      If Not Exist "!dbpath!%%m\%%c" (
          Echo Entferne %%m/%%c>>%~dp0%KellermusikLog.txt
          del "!tpath!%%m\%%c"
      ) 
    )
  )
  REM Neubennenung der Wiedergabeliste, falls ausgeschlossene Ordner vorhanden und Neubennenung in INI aktiviert
  If !renexcl!==true (
    If !eexcl!==true (
      For /D %%g in ("*") do (
       Set /A "exclvar=0"
       For /F "delims= skip=4 eol= tokens=*" %%y in (Exclude.ini) do (
        If %%g==%%y (
          Set /A "exclvar=1"
        )
        )
        If !exclvar!==0 (
          Set "plname=!plname!_%%g"
        )
      )
    )
  )
  REM Wiedergabelistenerstellung nach dem Zufallsprinzip ohne Zuruecklegen
  REM Erste Zufallszahl immer aehnlich, da auf aktueller Systemzeit basierend, deshalb einmalige Abfrage von random fuer zukuenftige, korrekte Zufallszahlen noetig
  Echo Initialisierung des Zufallgenerators: !random!>>%~dp0%KellermusikLog.txt
  REM Uberpruefung auf vorrangegangenen Skriptabbruch bei der Wiedergabelistenerstellung
  If Not Exist "ResumeAfterError.txt" (
    Echo #EXTM3U>!tpath!!plname!.m3u
    Echo Tracks werden der Wiedergabeliste hinzugefuegt
    Set /A "resumetracks=0"
  ) Else (
  Echo Unvollstaendige Wiedergabeliste entdeckt
  Echo Erstellung der Wiedergabeliste wird fortgesetzt
  )
  Echo Bitte warten
  REM Checke alle Ordner im Zielverzeichnis
  For /D %%g in ("*") do (
    If Exist "ResumeAfterError.txt" (
      For /F %%o in (ResumeAfterError.txt) do (
        REM Ueberpruefung in welchem Ordner fortgefahren werden soll
        If %%g==%%o (
          REM Variable zur Sicherstellung des Fortsetzens der Zufallstrackwahl, statt neuer Trackwahl fuer den betreffenden Ordner
          Set /A "resumetracks=1"
          Del "ResumeAfterError.txt"
          Echo Nach Fehler mit Ordner %%g fortgefahren>>%~dp0%KellermusikLog.txt
        )
      )
    )
    If Not Exist "ResumeAfterError.txt" (
      REM Ausschluss der per INI definierten Ordner via Ausschlussvariable und FOR-Check
      Set /A "exclvar=0"
      If !eexcl!==true (
        For /F "delims= skip=4 eol= tokens=*" %%y in (Exclude.ini) do (
          If %%g==%%y (
            Set /A "exclvar=1"
          )
        )
      )
      If !exclvar!==0 (
        REM Abfrage der Dateienanzahl im betreffenden Ordner
        Set /A "dateien=0"
        For /F "tokens=*" %%h in ('dir /b %%g') do (
          Set /A "dateien+=1"
        )
        REM Direktes Schreiben aller Dateien in die Wiedergabeliste, falls weniger oder gleich der in TracksPP spezifizierten Anzahl
        If !dateien! leq !trackspp! (
          For /F "tokens=*" %%h in ('dir /b %%g') do (
          Echo F!varue!ge %%g/%%h der Wiedergabeliste hinzu>>%~dp0%KellermusikLog.txt
          Echo %%g/%%h>>!tpath!!plname!.m3u
          )
        ) Else (
          REM Temporaeres Setzen von TracksPP auf die Anzahl noch verbleibender Titel, falls zuvor Erstellung der Wiedergabeliste abgebrochen
          If !resumetracks!==1 (
            Set /A "tracksppold=!trackspp!"
            For /F %%j in (RandomTrackSelector.txt) do (
              Set /A "trackspp-=1"
            )
            Echo Nach Fehler mit Auswahl von !trackspp! Tracks fortgefahren>>%~dp0%KellermusikLog.txt
            REM Zusaetzlicher Durchgang in diesem Fall noetig, da erster Durchgang bei Fortsetzen der Wiedergabeerstellung nicht gewertet wird, siehe unten
            Set /A "trackspp+=1"
          )
          REM Wiederholung der Zufallsauswahl fuer die gewuenschte Anzahl an Tracks pro Person
          For /L %%t in (1,1,!trackspp!) do (
            REM Generierung einer Zufallszahl zwischen 1 und der Dateianzahl des Ordners
            Set /A "randomtrack=(!random!*!dateien!/32767)+1"
            REM Im ersten Durchgang, falls kein vorangegangener Abbruch des Skriptesm immer nicht wiederholende Zufallszahl, deshalb keine weitere Ueberpruefung notwendig
            If %%t==1 (
              If !resumetracks!==0 (
                REM Schreiben der generierten Zufallszahlen in ein temporaeres Textdokument zum Auslesen mittels FOR
                Echo !randomtrack!>RandomTrackSelector.txt
              )
            ) Else (
            REM Ueberpruefung der Einzigartigkeit der generierten Zufallszahl mittels temporaerem Textdokument
            For /F "tokens=*" %%u in (RandomTrackSelector.txt) do (
              If %%u==!randomtrack! (
                REM Wierholung der Generierung einer Zufallszahl in einer in der INI speziefizierten maximalen Anzahl an Zyklen, falls nicht einzigartig
                Set /A "stopv=0"
                For /L %%r in (1,1,!rsi!) do (
                  REM Da Ablaufen von FOR immer bis zum letzten Zyklus keine weitere Codeausfuehrung, wenn Stoppvariable gesetzt
                  If !stopv!==0 (
                    Set /A "randomtrack=(!random!*!dateien!/32767)+1"
                    Set /A "stopv=1"
                    For /F "tokens=*" %%z in (RandomTrackSelector.txt) do (
                      If %%z==!randomtrack! (
                        Set /A "stopv=0"
                      )
                    )
                    REM Wenn letzter Zyklus und noch immer keine Stoppvariable gesetzt, Neustart des Skripts und potentielle Aktivierung des EasterEggs
                    If %%r==!rsi! (
                      If !stopv!==0 (
                        REM Setzen des momentan verarbeiteten Ordners in einer temporaeren Textdatei zur Fortsetzung nach Abbruch; Textdatei ebenfalls zur generellen Erkennung eines Skriptabbruchs
                        Echo %%g>ResumeAfterError.txt
                        Echo Log bei Neustart des Skripts fortsetzen>%~dp0%ResumeLog.txt
                        If !easter!==true (
                          Echo Fatal Error>FatalError.txt
                          Echo Yuno smashed the wall of time and space with a hammer>>FatalError.txt
                          Echo This event is not supposed to happen>>FatalError.txt
                          Echo WAIT>>FatalError.txt
                          Echo WAIT>>FatalError.txt
                          Echo WAIT>>FatalError.txt
                          Echo Restarting the third worlds fate>>FatalError.txt
                          Echo Beware, this process may take up to 10000 years>>FatalError.txt
                          Echo P>>FatalError.txt
                          Echo L>>FatalError.txt
                          Echo E>>FatalError.txt
                          Echo A>>FatalError.txt
                          Echo S>>FatalError.txt
                          Echo E>>FatalError.txt
                          Echo  W>>FatalError.txt
                          Echo  A>>FatalError.txt
                          Echo  I>>FatalError.txt
                          Echo  T>>FatalError.txt
                          Echo Second god detected>>FatalError.txt
                          Echo P>>FatalError.txt
                          Echo L>>FatalError.txt
                          Echo E>>FatalError.txt
                          Echo A>>FatalError.txt
                          Echo S>>FatalError.txt
                          Echo E>>FatalError.txt
                          Echo  W>>FatalError.txt
                          Echo  A>>FatalError.txt
                          Echo  I>>FatalError.txt
                          Echo  T>>FatalError.txt
                          Echo Evaluating new data finished>>FatalError.txt
                          Echo Initializing recalculated third world>>FatalError.txt
                          Echo Fatal Error
                          timeout 3  > NUL
                          Echo Yuno smashed the wall of time and space with a hammer
                          timeout 3  > NUL
                          Echo This event is not supposed to happen
                          timeout 3  > NUL
                          Echo WAIT
                          timeout 1  > NUL
                          Echo WAIT
                          timeout 1  > NUL
                          Echo WAIT
                          timeout 1  > NUL
                          Echo Restarting the third worlds fate
                          timeout 3  > NUL
                          Echo Beware, this process may take up to 10000 years
                          timeout 3  > NUL
                          Echo    P
                          Echo    L
                          Echo    E
                          Echo    A
                          Echo    S
                          Echo    E
                          timeout 2  > NUL
                          Echo     W
                          Echo     A
                          Echo     I
                          Echo     T
                          timeout 7  > NUL
                          Echo Unexpected presence of a second god detected
                          timeout 3  > NUL
                          Echo      P
                          Echo      L
                          Echo      E
                          Echo      A
                          Echo      S
                          Echo      E
                          timeout 2  > NUL
                          Echo       W
                          Echo       A
                          Echo       I
                          Echo       T
                          timeout 7  > NUL
                          Echo Evaluating new data finished
                          timeout 3  > NUL
                          Echo Initializing recalculated third world
                          timeout 3  > NUL
                          timeout 5
                        )
                      Start "Recalculated Third World Script" /D "!tpath!" "%~dpnx0%"
                      Endlocal
                      Exit 
                      )
                    ) 
                  )
                )                    
              )
            )
            REM Schreiben der einzigartigen Zufallszahl in die temporaere Textdatei zur Trackauswahl
            Echo !randomtrack!>>RandomTrackSelector.txt
            )
            REM Falls keine Fortsetzung der Trackauswahl, Auslesen der Zufallszahl und Wahl des korrespondierenden Tracks ueber FOR
            If !resumetracks!==0 (
              Set /A "tracknumber=0"
              For /F "tokens=*" %%h in ('dir /b %%g') do (
                Set /A "tracknumber+=1"
                If !tracknumber!==!randomtrack! (
                  Echo F!varue!ge %%g/%%h der Wiedergabeliste hinzu>>%~dp0%KellermusikLog.txt
                  Echo %%g/%%h>>!tpath!!plname!.m3u
                )
              )
            ) Else (
              REM Falls Fortsetzung der Trackauswahl nach vorangegangenem Abbruch zuruecksetzen von TracksPP (keine Auswirkung auf momentane FOR-Schleife)
              Set /A "trackspp=!tracksppold!"
              REM Loeschen der Fortsetzungsvariable, zur Ermoeglichung einer normalen Trackauswahl basierend auf der augenblicklichen, modifizierten FOR-Schleife
              Set /A "resumetracks=0"
              Echo TracksPP auf !trackspp! zur!varue!ckgesetzt>>%~dp0%KellermusikLog.txt
            )
          )
        )
      ) Else (
        Echo Ausgeschlossener Ordner: %%g>>%~dp0%KellermusikLog.txt
      )
    )
  )
  REM Entfernen der temporaeren Trackauswahltextdatei nach erfolgreichem Abschluss des Vorgangs
  Del "RandomTrackSelector.txt"
  Echo Vorgang abgeschlossen
  Echo Beende
)
Endlocal
Exit