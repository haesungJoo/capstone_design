package ai.kitt.snowboy;

public enum  MsgEnum {
    MSG_VAD_END,
    MSG_VAD_NOSPEECH,
    MSG_VAD_SPEECH,
    MSG_VOLUME_NOTIFY,
    MSG_WAV_DATAINFO,
    MSG_RECORD_START,
    MSG_RECORD_STOP,
    MSG_ACTIVE,
    MSG_ERROR,
    MSG_INFO,
    MSG_TIMER,
    MSG_STOP,
    MSG_TIMER_ERROR,
    MSG_MODEL_GENERATED,
    MSG_ERROR_SHORT_HOTWORD,
    MSG_ERROR_NOFILE;

    public static MsgEnum getMsgEnum(int i) {
        return MsgEnum.values()[i];
    }
}
