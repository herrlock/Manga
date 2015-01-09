package de.herrlock.manga.std;

import static de.herrlock.manga.util.Logger.L;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Constants;

public class Ctrl
{
    private static final int urlIndex = 1;

    public static void main(String[] args)
    {
        L.trace("<main>").addSpace();
        new Ctrl().run();
        L.removeSpace().trace("</main>");
    }

    // private File path;
    private URL url;

    public Ctrl()
    {
        try
        {
            // this.path = new File("C:\\Users\\Jan\\Desktop\\mangatest\\");
            this.url = new URL(Constants.EXAMPLE_URLS[urlIndex]);
        }
        catch (MalformedURLException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public void run()
    {
        L.trace("<run>").addSpace();
        try
        {
            getChapterList();
            if (goon())
            {
                getPictureLinks();
                if (goon())
                {
                    getPictures();
                }
                else
                {
                    System.out.println("bye");
                }
            }
            else
            {
                System.out.println("bye");
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            L.removeSpace().trace("</run>");
        }
    }

    private static boolean goon()
    {
        System.out.println("go on? y|n");
        try (Scanner sc = new Scanner(System.in))
        {
            switch (sc.next(".+").charAt(0))
            {
                case 'y':
                case 'Y':
                    return true;
                default:
                    return false;
            }
        }
    }

    private void getChapterList() throws IOException
    {
        ChapterList cl = ChapterList.getInstance(this.url, null);
        for (Chapter chapter : cl)
        {
            L.error(chapter);
        }
    }

    private void getPictureLinks()
    {
        // TODO
    }

    private void getPictures()
    {
        // TODO
    }

}
