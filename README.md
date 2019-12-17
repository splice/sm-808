# SM-808 Practice exercise

### Getting started
#### Running
1. Install [JDK 8+](https://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Gradle](https://gradle.org/install/)
2. `./gradlew run -q --console=plain`

#### Testing
`./gradlew test`

#### Using the MIDI output device
This program provides the option of outputting a sequence as MIDI. You may want to set up a virtual MIDI interface so that the MIDI output can get picked up by another program running on your computer. In my case, I followed [these instructions](https://dialogaudio.com/modulationprocessor/guides/virtual_midi/virtual_midi_setup.php) to set up a virtual MIDI port on my mac and configured Superior Drummer to use that port as its input. The mapping of events to MIDI note values is defined in the MidiOutputDevice; you may wish to tweak these values depending on your configuration.

### Changelog
#### 2.0.0
- Adds audio and MIDI output devices.
- Allows realtime sequence editing for non-console output modes.

#### 1.0.0
- Adds console-based UI for building a sequence, and outputs the sequence to console. 

### Notes for reviewers
#### Design choices
Language choice: Java. Partially because it enables strongly typed interfaces, which makes implementing the strategy pattern (e.g. for `OutputDevice`s) clean, and partially because the JVM makes this code platform agnostic.

I chose a fairly simple approach to this problem, with the intention of making it extensible so that new features can easily be added without making drastic changes to the underlying software components. There are 3 main components to this program:
1. `SequenceBuilder` interface: provides a UI for building a sequence
2. `Sequencer`:  models the sequence itself and provides functionality for starting and stopping the sequence
3. `OutputDevice` interface: provides logic for outputting the sequence to the user

Version 2.0.0 of this program has a few limitations - it collects the sequence from the user via console which is a poor UX, and it can only build an 8-step sequence. However, due to the extensibility of this modular design, it should be simple to swap out the UI for something more user-friendly and build more sophisticated sequences.

#### Follow-ups and potential features
- Allow user to specify the number of beats & subdivisions per beat
- Allow user to specify time signature note value for tempo (quarter, eighth, dotted eighth, etc.)
- Allow user to tap a key to specify tempo
- Add a more user-friendly UI
- Technical: Use dependency injection to simplify testing of output devices.

# Original question
The goal of this exercise is to practice designing models and
interfaces.

There aren't good or bad solutions, there are solutions that
match the requirements and some that don't. There are solutions that
might be considered elegant by some and solutions that would be
considered clever.

## Building a Drum Machine

This exercise assumes you are somewhat familiar with drum machines.
If you aren't
please read http://en.wikipedia.org/wiki/Drum_machine

Your challenge is to code the sequencer part of our Drum Machine which
we called SM-808. More precisely you will need to sequence and "play"
your own version of the famous [four-on-the-floor](http://en.wikipedia.org/wiki/Four_on_the_floor_(music)) rhythm pattern.

![Example pattern to implement](/Four_to_the_floor_Roland_TR-707.jpg?raw=true)

Note that instead of hearing an actual sound, you are expected to
generate a real time visual representation of the sequence being played.

### Expected output

Here is some pseudo code showing the expected interaction and one way to
represent the output (you are encouraged to find a better way).

You can use a command line output, work in the browser or use an electric
circuit.

Use whatever programming language you want, as long as we can execute your
solution.

```
// create a new song
song = Song -> bpm: 128, title: Animal Rights

// add the 3 patterns
song <- Kick   |X|_|_|_|X|_|_|_|
song <- Snare  |_|_|_|_|X|_|_|_|
song <- HiHat  |_|_|X|_|_|_|X|_|

song.play

// -> real time output
// |Kick|_|HiHat|_|Kick+Snare|_|HiHat|_|Kick|_|HiHat|.....
```

![Output example](/drummachine-kata.gif?raw=true)

(ignore the audio outputs print at the beginning but pay attention to
the output speed at a BPM of 128)

### Extra info

* A song contains multiple patterns being sequenced for different
  samples.
* A song plays at a given tempo (AKA bpm), the tempo does not need to be able
  change while the song plays.
* For this exercise, you are expected to implement 3 patterns for the following
  sounds/samples: kick, snare and hihat (you can use the example pattern or
  come up with your own).
* The time signature is expected to be 4/4 (if you don't know what that is,
  don't worry and ignore this instruction).
* The pattern is expected to be 8 steps or more.
* Your code will be executed on the command line or in the browser.
* Try to keep external dependencies to a minimum.
* See next section to see what to output.
* The output isn't expected to be in sync with the tempo/BPM, but if you'd like
  to do so, go for it!


### Timing information

Outputting the patterns in tempo isn't required for this exercise, but if you
wish to do so you might need some extra information.

At a 4/4 time signature of 60 BPM (beats per minute), we get 1 beat per second.
We can assume that 8 steps = 1 bar, representing 4 beats.

In other words, a 8 step pattern would take `(60/BPM)*4` seconds to play and
each step would take `((60/BPM)*4)/8` seconds.


### If you're having fun

**If you're submitting this exercise to apply for a job at Splice, please know
that under no circumstances are any of these things expected or required. These
are just some ideas for extending your work, if you're having fun with the
project!**

* Try mixing and matching patterns of different durations (8, 16, 32 steps),
  note that if you have 2 patterns, one 8 and one 16, the 8 should play
  twice while the 16 plays once.
* Add support for velocity (the amplitude/volume of a note).
* Try to output sound (macOS has the `say` command, Windows has `ptts`,
  also most language have bindings for [portaudio](http://www.portaudio.com/)

If you can't stop:

* How about live play? Can you allow users to add/remove/change patterns
  while playing?
* Make it even more interactive and add a web interface/GUI!


### Splice Evaluation

If you are given this exercise as a code challenge, we are going to anonymize
your submission and evaluate it using a rubric. There is no specific language
requirement for the submission. **Please use the language with which you are
most comfortable working, as we'd like to see how you'd approach something when
working with familiar tools.** Provided things look good, we'll set up a time to
meet with two backend engineers to chat about the approach you took, and answer
any questions you may have.

So that you're prepared, here is an overview of the things we'll be evaluating
and discussing about your submission:

* How much time did you spend on the exercise? What parts took longer?
* What were the hard parts, and what parts did you enjoy most?
* How thoroughly is the submission documented?
* How well is the submission tested?
* How clearly are the key concepts modeled?
* What kinds of trade-offs were made in balancing simplicity and flexibility?
  Can your solution be easily extended to support a change in requirements?
* Is the code written in a readable and consistent style, such that someone
  familiar with the language would understand what you're doing easily?

### Submit your solution

If you want your solution to be reviewed you can:

* email the hiring manager your implementation as a zip or link to your repo
  (for candidates we are interviewing only)
* fork this repo and send a PR (community reviewed)
* create an issue with the url for your implementation (community reviewed)
