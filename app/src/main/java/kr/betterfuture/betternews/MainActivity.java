package kr.betterfuture.betternews;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kr.betterfuture.betternews.adapter.ArticleAdapter;
import kr.betterfuture.betternews.helper.NetHelper;
import kr.betterfuture.betternews.model.Article;

public class MainActivity extends ActionBarActivity {
    public MainActivity() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listViewArticle);

            final ArrayList<Article> list = new ArrayList<Article>();

            String r = NetHelper.SendRESTRequest("http://betterfuture.kr/xe/index.php?mid=news_issue");

            Source source = new Source(r);

            List<Element> elementList = source.getAllElements(HTMLElementName.LI);

            Iterator elementIter = elementList.iterator();
            elementIter.next();

            while(elementIter.hasNext()) {
                Element data = (Element) elementIter.next();

                System.out.println(data.getContent());
                System.out.println("Source text with content:\n" + data);


                if(!data.getContent().getAllElements(HTMLElementName.H2).isEmpty()){
                    Article a = new Article();
                    a.Title = data.getContent().getFirstElementByClass("ngeb").getTextExtractor().toString();
                    a.Desc = data.getContent().getFirstElementByClass("cnt").getTextExtractor().toString();
                    a.Url = data.getContent().getFirstElement(HTMLElementName.A).getAttributeValue("href");

                    List<Element> info = data.getContent().getFirstElementByClass("info").getAllElements("b");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                    String date = info.get(0).getTextExtractor().toString();
                    try {
                        a.WhenCreated = format.parse(date);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                    a.Views = Integer.parseInt(info.get(2).getTextExtractor().toString());
                    list.add(a);
                }

            }

/*

            List<Element> elementList = source.getAllElements();
            for (Element element : elementList) {
                boolean isLineItem = false;
                if (element.getAttributes() != null) {
                    for (Attribute a : element.getAttributes()) {
                        if (a.getName().equals("class") && a.getValue().equals("rt_area")) {
                            System.out.println("-------------------------------------------------------------------------------");
                            isLineItem = true;
                        }
                    }
                }

                if (isLineItem) {
                    System.out.println(element.getContent());
                    System.out.println("Source text with content:\n" + element);

                    Article a = new Article();

                    a.Title = element.getAllElements("h2").get(0).getContent().toString();

                    for (Element div : element.getAllElements("div")) {
                        if (div.getAttributes() != null) {
                            boolean isDesc = false;
                            for (Attribute att : div.getAttributes()) {
                                if (att.getName().equals("class") && att.getValue().equals("cnt")) {

                                    isDesc = true;
                                }
                            }

                            if (isDesc)
                                a.Desc = div.getContent().toString();
                        }
                    }


                    list.add(a);
                    a.WhenCreated = new Date();
                }

                //System.out.println(element.getDebugInfo());
                */
/*if (element.getAttributes() != null)
                    System.out.println("XHTML StartTag:\n" + element.getStartTag().tidy(true));
                System.out.println("Source text with content:\n" + element);*//*

            }
*/

            ArticleAdapter adapter = new ArticleAdapter(getActivity(),
                    R.layout.article_list_item, R.id.textViewTitle,
                    list);
            listView.setAdapter(adapter);


            return rootView;
        }
    }
}
