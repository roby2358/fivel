package yuwakisa.servel.mcp

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import yuwakisa.servel.Content

class ConfigServlet extends HttpServlet:
  override protected def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit =
    val config = Map(
      "defaultEnvironment" -> Map(
        "HOME" -> "/home/vagrant",
        "LOGNAME" -> "vagrant",
        "PATH" -> "/home/vagrant/.npm/_npx/9eac9498388ae25e/node_modules/.bin:/home/vagrant/node_modules/.bin:/home/node_modules/.bin:/node_modules/.bin:/usr/lib/node_modules/npm/node_modules/@npmcli/run-script/lib/node-gyp-bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin",
        "SHELL" -> "/bin/bash",
        "TERM" -> "xterm-256color",
        "USER" -> "vagrant"
      ),
      "defaultCommand" -> "",
      "defaultArgs" -> ""
    )
    Content.okJson(response, config) 