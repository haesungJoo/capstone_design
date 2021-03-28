package ai.kitt.snowboy;
import java.io.File;
import android.os.Environment;

public class Constants {
    public static final String ASSETS_RES_DIR = "snowboy";
    public static final String DEFAULT_WORK_SPACE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/snowboy/";
    public static final String ACTIVE_UMDL = "alexa.umdl";
    public static final String HELPME_PMDL = "helpeme_ko.pmdl";
    public static final String HELPMEMIN_PMDL = "helpme_min.pmdl";
    public static final String SALREOUJEOU_PMDL = "살려줘.pmdl";
    public static final String SALREOUJUSAEYU_PMDL = "salreoujusaeyu.pmdl";
    public static final String TEST_PMDL = "salreoujusaeyu.pmdl";
    public static final String ACTIVE_RES = "common.res";
    public static final String SAVE_AUDIO = Constants.DEFAULT_WORK_SPACE + File.separatorChar + "recording.pcm";
    public static final int SAMPLE_RATE = 16000;
}
