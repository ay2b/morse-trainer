Morse-Trainer was born out of my desire to re-learn morse code, combined
with a desire to learn about making sound in Java.  It uses a "flash card"
style approach to learning.  Given a list of words and/or characters, it
will play the Morse, then speak (using a text-to-speach library) the word
and print it to the terminal, then play the Morse again.  The idea is to
let this run in the background throughout the day to gradually build
recognition.  The philosophy is that Morse should be learned "by ear", and
so this program imitates various "language learning tapes" that I have
seen/used over the years.

The program can be build with `./build.sh` or `./gradlew clean build`.

For full usage, run `java -jar ./build/libs/morse-trainer-0.1.1-SNAPSHOT.jar`

For my training, I like to use something like `head -50 src/test/resources/1-1000.txt | java -jar ./build/libs/morse-trainer-0.1.1-SNAPSHOT.jar -l -n -v 0.1 -w /dev/stdin`
