using System.Diagnostics;

namespace MD
{
	class Run
	{
		static void Main()
		{
            ProcessStartInfo startInfo = new ProcessStartInfo("cmd.exe", "/C java -cp \"lib/*\" de.herrlock.manga.Ctrl");
            startInfo.WindowStyle = ProcessWindowStyle.Hidden;
            Process process = new Process();
            process.StartInfo = startInfo;
            process.Start();
		}
	}
}