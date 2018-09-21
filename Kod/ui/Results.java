package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import ui.util.Constants;
import ui.util.ResultInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Results extends JFrame {

    public Results(String title) {
        setTitle(title);

        GridLayout gridLayout = new GridLayout(Constants.RESULTS_ROWS, Constants.RESULTS_COLS, Constants.RESULTS_HGAP, Constants.RESULTS_VGAP);
        setLayout(gridLayout);
    }

    public void init(List<List<ResultInfo>> results) {
        for (List<ResultInfo> list : results) {
            ChartPanel chartPanel = new ChartPanel(createChart(list));
            chartPanel.setBorder(BorderFactory.createEmptyBorder(Constants.BORDER_DIMEN, Constants.BORDER_DIMEN, Constants.BORDER_DIMEN, Constants.BORDER_DIMEN));
            chartPanel.setBackground(Color.white);
            add(chartPanel);
        }
    }

    private JFreeChart createChart(List<ResultInfo> infos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String rowKey = null;
        switch (infos.get(0).type) {
            case EXPLOITATION: {
                rowKey = Constants.EXPLOITATION;
                break;
            }

            case THROUGHPUT: {
                rowKey = Constants.THROUGHPUT;
                break;
            }

            case RESPONSE_TIME: {
                rowKey = Constants.RESPONSE_TIME;
                break;
            }

            default: // Big error; should not happen
        }

        for (ResultInfo info : infos) {
            dataset.setValue(info.value, rowKey, info.componentId);
        }

        return ChartFactory.createBarChart(
            Constants.EMPTY_STRING,
            Constants.EMPTY_STRING,
            rowKey,
            dataset,
            PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
    }

}