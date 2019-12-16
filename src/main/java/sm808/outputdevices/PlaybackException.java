package sm808.outputdevices;

import javax.sound.sampled.LineUnavailableException;

public class PlaybackException extends Exception {
    public PlaybackException(Throwable cause) {
        super(cause);
    }
}
