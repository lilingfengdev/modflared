# Modflared
Automatically connects you to a [Cloudflare tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/) without having to install [cloudflared](https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/install-and-setup/installation/) separately.
#### This mod has not yet been fully tested!

## How to use
To be able to use the mod you have to be on the operating system Windows, Linux, or MacOS.

## Other resources
For more detailed instructions, you can also read Varun A's blog post. The blog post can be found [here](https://dacubeking.com/2024/02/28/Proxying-Minecraft.html).

## Configuring Cloudflared
You need to set up cloudflare on your server for this to work. There's plenty of guides on how to do this elsewhere.  
Make sure in your config file (possibly in `/etc/cloudflared/config.yml`) you have the lines:
```YML
- hostname: example.domain.net
  service: tcp://localhost:25565
```
Replace `example.domain.net` with the correct subdomain you want to use. If you're running multiple instances (eg. with docker), change the port 25565 to whatever port you're using.  
Restart the cloudflare daemon (`sudo systemctl restart cloudflared`) to apply the changes.
Add the correct DNS entry: go to [Cloudflare dashboard](https://dash.cloudflare.net), go to your website and DNS entries, then add a new CNAME DNS entry with your subdomain and set the target to `<tunnelID>.cfargotunnel.com`, with the tunnel ID found in the cloudflare config.yml file.
### Example DNS Entries
![Example CNAME](https://raw.githubusercontent.com/HttpRafa/modflared/multi/latest/.github/images/dns_cname.png)
![Example TXT](https://raw.githubusercontent.com/HttpRafa/modflared/multi/latest/.github/images/dns_txt.png)

# Versions 1.0.0 and onward
### For Players
If your server admin has properly configured their server you should be able to connect to the server as usual. 
No extra configuration is needed.

If your server admin has not properly configured their server, you will need to add their server to the `forced_tunnels.json` file in the modflared config folder. 
#### Example configuration (modflared/forced_tunnels.json)
```JSON
["example.domain.net", "example2.domain.net"]
```

### Server Admins (Setting Up cloudflared)
Install Cloudflared as described in the “Configuring Cloudflared” part above.

#### Telling modflared to use connect to your server using cloudflared
For the hostname that you want your players to connect to (eg. `example.domain.net`), create a TXT dns record with either of the following values:
- `cloudflared-use-tunnel` - This will make modflared connect to the tunnel on the hostname itself (eg. `example.domain.net`)
- `cloudflared-route=<route>` - This will make modflared connect to the tunnel under the hostname of `<route>`. 
(ex. setting `cloudflared-route=example2.domain.net` will make modflared connect to the tunnel on `example2.domain.net` instead of `example.domain.net`)

# Versions below 1.0.0
### For Players
To give the mod information about the tunnel, you have to create a folder called "modflared" in the Minecraft folder. A file called "access.json" must then be created in this folder.
#### Example configuration (access.json)
```JSON
[
  {
    "use": true,
    "protocol": "tcp",
    "hostname": "example.domain.net",
    "bind": {
      "host": "127.0.0.1",
      "port": 25565
    }
  }
]
```
#### Connect to the server
After you have set everything up with the access.json, you can connect to the server via the tunnel. Enter 127.0.0.1 and the port that is in the access.json (in the example 25565) into minecraft and click connect.
![Minecraft Join](https://raw.githubusercontent.com/HttpRafa/modflared/multi/latest/.github/images/minecraft_localhost_connect.png)

### Server Admins (Setting Up cloudflared)
On the server side, you only need to install Cloudflared as described above in the “Configuring Cloudflared” part.