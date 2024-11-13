<!DOCTYPE html>
<html lang="en" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="x-apple-disable-message-reformatting">
    <title>ChainTest</title>
    <style type="text/css">
      td, th, div, p {font-family: -apple-system, system-ui, "Lato", "Helvetica Neue", "Segoe UI", Arial, sans-serif;}
      .bg-pass { background-color: #79B530; color: #fff; }
      .bg-fail { background-color: #e64b5d; color: #fff; }
      .bg-skip { background-color: #e6e04c; color: #fff; }
      .tag { background-color:#f6f7f9; }
    </style>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" yahoo="fix" bgcolor="#F7F8F9" style="box-sizing:border-box;margin:0;padding:0;width:100%;word-break:break-word;-webkit-font-smoothing:antialiased;">
    <!-- title -->
    <table class="wrapper" cellpadding="0" cellspacing="0" role="presentation" width="100%">
      <tr>
        <td align="center">
          <table>
            <tr>
              <td width="650">
                <table  style="padding: 20px;">
                  <tr>
                    <td>
                      <span class="bg-${build.result?lower_case}" style="font-size:11px;font-weight:500;margin-top:0;padding:0px 5px;">Build ${build.result}</span>
                      <br/>
                      <span style="font-size:12px;font-weight:500;color:#555;">${build.startedAt} // ${build.durationMs}</span>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>

    <!-- stats -->
    <table align="center" width="650" style="margin-bottom:2px;">
	  <tbody>
	    <tr>
	      <td align="center" bgcolor="#FFFFFF">
	        <table width="100%">
	          <tbody>
	            <tr>
	              <td width="100" style="padding:30px 20px 30px 50px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;margin-bottom:15px;">Tests</p>
	                <h1 style="font-size:34px;font-weight:500;color:#292929;margin:0;">${build.runStats[0].total}</h1>
	              </td>
	              <td width="100" style="padding:30px 20px 30px 50px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;margin-bottom:15px;color:#79B530">Passed</p>
	                <h1 style="font-size:34px;font-weight:500;color:#292929;margin:0;">${build.runStats[0].passed}</h1>
	              </td>
	              <td width="100" style="padding:30px 20px 30px 50px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;margin-bottom:15px;color:#e64b5d">Failed</p>
	                <h1 style="font-size:34px;font-weight:500;color:#292929;margin:0;">${build.runStats[0].failed}</h1>
	              </td>
	              <td width="100" style="padding:30px 20px 30px 50px;">
	                <p style="font-weight:500;color:#7E7E7E;margin:0;margin-bottom:15px;color:#e6e04c">Skipped</p>
	                <h1 style="font-size:34px;font-weight:500;color:#292929;margin:0;">${build.runStats[0].skipped}</h1>
	              </td>
	            </tr>
	          </tbody>
	        </table>
	      </td>
	    </tr>
	  </tbody>
	</table>

    <!-- tags -->
    <#if build.tagStats??>
    <table width="100%">
	  <tr>
	    <td align="center">
	      <table width="650">
	        <tr>
	          <td bgcolor="#FFFFFF" style="padding:20px 40px;font-size:14px;">
	            <span style="font-size:20px;color:#89C5CC;font-weight:500;">Tags</span>
	            <table><tr><td style="height:10px;"></td></tr></table>
	            <table>
	              <tr style="font-weight:500;">
	                <td width="250">Name</td>
	                <td width="23"></td>
	                <td style="" width="60">Passed</td>
	                <td class="padding" width="10"></td>
	                <td style="" width="60">Failed</td>
	                <td class="padding" width="10"></td>
	                <td style="" width="60">Skipped</td>
	              </tr>
	              <#list build.tagStats as tag>
	              <tr style="height:10px;"></tr>
	              <tr>
	                <td width="250">${tag.name}</td>
                    <td width="23"></td>
                    <td style="" width="60">${tag.passed}</td>
                    <td class="padding" width="10"></td>
                    <td style="" width="60">${tag.failed}</td>
                    <td class="padding" width="10"></td>
                    <td style="" width="60">${tag.skipped}</td>
	              </tr>
	              </#list>
	            </table>
	          </td>
	        </tr>
	      </table>
	    </td>
	  </tr>
	</table>
	</#if>

    <!-- tests -->
    <table width="100%">
      <tr>
        <td align="center">
          <#list tests as test>
          <table width="650">
            <tr>
              <td bgcolor="#FFFFFF" style="padding:20px 40px;">
                <table>
                  <tr>
                    <td width="650px">
                      <p style="font-size:16px;font-weight:500;margin:0;margin-bottom:5px;">
                        ${test.name}
                      <p style="font-size:12px;margin:0;">
                        ${test.startedAt?string("MM-dd-yyyy HH:mm:ss")} / ${test.durationMs?number_to_time?string("mm:ss:SSS")}
                      </p>
                      <p style="margin:10px 0;font-size:12px;">
                        <span class="bg-${test.result?lower_case}" style="font-size:12px;margin:0;padding:0 7px;">${test.result}</span>
                      </p>
                    </td>
                  </tr>
                  <#list test.children as child>
                  <tr>
                    <td width="650px" style="padding:5px 0;">
                      <p style="font-size:16px;font-weight:500;margin:0;margin-bottom:5px;">
                        - ${child.name}
                      </p>
                      <p style="font-size:12px;margin:0;margin-left:10px;">
                        ${child.startedAt?string("MM-dd-yyyy HH:mm:ss")} / ${child.durationMs?number_to_time?string("mm:ss:SSS")}
                      </p>
                      <p style="margin:10px 0;font-size:12px;margin-left:10px;">
                        <span class="bg-${child.result?lower_case}" style="font-size:12px;margin:0;padding:0 7px;">${child.result}</span>
                      </p>
                    </td>
                  </tr>
                  </#list>
                </table>
              </td>
            </tr>
          </table>
          </#list>
        </td>
      </tr>
    </table>
    <table><tr><td height="100"></td></tr></table>
  </body>
</html>