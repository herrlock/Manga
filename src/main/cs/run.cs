using System.Diagnostics;

namespace MD
{
	class Run
	{
		static void Main()
		{
            ProcessStartInfo startInfo = new ProcessStartInfo("cmd.exe", "/C java -jar MangaLauncher.jar");
            startInfo.WindowStyle = ProcessWindowStyle.Hidden;
            Process process = new Process();
            process.StartInfo = startInfo;
            process.Start();
		}
	}
}