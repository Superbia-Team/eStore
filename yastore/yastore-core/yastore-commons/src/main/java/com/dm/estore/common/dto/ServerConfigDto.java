package com.dm.estore.common.dto;

import java.io.Serializable;

public class ServerConfigDto implements Serializable {

    private static final long serialVersionUID = 2349558364716920658L;

    private int serverPort;
    private int serverPortSsl;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPortSsl() {
        return serverPortSsl;
    }

    public void setServerPortSsl(int serverPortSsl) {
        this.serverPortSsl = serverPortSsl;
    }
}
