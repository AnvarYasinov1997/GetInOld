package com.wellcome.main.util

import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import java.io.IOException
import java.net.*

class DefaultProxySelector(private val defSel: ProxySelector,
                           private val loggerService: LoggerService) : ProxySelector() {

    private val proxies = mutableListOf<Proxy>()

    init {
        proxies.addAll(arrayOf(
            Proxy(Proxy.Type.HTTP, InetSocketAddress("94.244.191.219.nash.net.ua", 3128)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("82.193.112.15.cl.ipnet.ua", 48173)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("176-102-48-105.retail.datagroup.ua", 61687)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("80.240.25.63.vultr.com", 1080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.88-198-50-103.clients.your-server.de", 3128)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.88-198-24-108.clients.your-server.de", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("node-83s.pool-1-2.dynamic.totinternet.net", 49371)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.88-198-50-103.clients.your-server.de", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.42.75.9.176.clients.your-server.de", 3128)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.170.119.9.176.clients.your-server.de", 3128)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.42.75.9.176.clients.your-server.de", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("ew.ptr215.ptrcloud.net", 80)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.170.119.9.176.clients.your-server.de", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("ip2.ip-51-38-162.eu", 32231)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("15-76.x-com.net.ua", 43448)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("207-0-213-178-static-client.sunline.ua", 44242)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("r195-157.uran.ru", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("ns3054841.ip-37-59-32.eu", 1080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("ipfoneweb.isncom.net", 80)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("server-46.45.146.213.as42926.net", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("server-46.45.146.210.as42926.net", 8080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("ip180.ip-176-31-69.eu", 1080)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("26-141-19-223-on-nets.com", 8197)),
            Proxy(Proxy.Type.HTTP, InetSocketAddress("static.88-198-24-108.clients.your-server.de", 3128))
        ))
    }

    override fun select(uri: URI): MutableList<Proxy> = if (proxies.isEmpty()) defSel.select(uri) else proxies

    override fun connectFailed(uri: URI, sa: SocketAddress, ioe: IOException) {
        loggerService.warning(LogMessage("Proxy failed $sa $ioe"))
        val i = proxies.indexOfFirst { it.address() == sa }
        if (i > -1) proxies.removeAt(i)
    }
}