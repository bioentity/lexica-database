package yeastmine;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.intermine.metadata.Model;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

public class QueryClient
{
    private static final String ROOT = "https://yeastmine.yeastgenome.org:443/yeastmine/service";

    public static void main(String[] args) throws IOException {
        ServiceFactory factory = new ServiceFactory(ROOT);
        Model model = factory.getModel();
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews("Gene.primaryIdentifier",
		       "Gene.symbol",
		       "Gene.sgdAlias",
		       "Gene.secondaryIdentifier");

        // Add orderby
        query.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);

        QueryService service = factory.getQueryService();
        PrintStream out = System.out;
        String format = "%-22.22s | %-22.22s | %-22.22s | %-22.22s\n";
        out.printf(format, query.getView().toArray());
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        while (rows.hasNext()) {
            out.printf(format, rows.next().toArray());
        }
        out.printf("%d rows\n", service.getCount(query));
    }

}